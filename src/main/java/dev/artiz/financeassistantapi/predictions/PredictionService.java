package dev.artiz.financeassistantapi.predictions;

import dev.artiz.financeassistantapi.exception.InsufficientDataException;
import dev.artiz.financeassistantapi.predictions.dto.PredictionDTO;
import dev.artiz.financeassistantapi.predictions.model.Prediction;
import dev.artiz.financeassistantapi.predictions.repository.PredictionRepository;
import dev.artiz.financeassistantapi.transactions.model.Transaction;
import dev.artiz.financeassistantapi.transactions.model.TransactionCategory;
import dev.artiz.financeassistantapi.transactions.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final TransactionRepository transactionRepository;
    private final PredictionRepository predictionRepository;
    private final List<PredictionStrategy> strategies;

    @Transactional
    public PredictionDTO.Prediction predictNextMonth(
        TransactionCategory category
    ) {
        List<Transaction> history =
            transactionRepository.findByCategoryOrderByCreatedAtAsc(category);

        if (history.size() < 3) {
            throw new InsufficientDataException(
                "Insufficient data for category: " + category
            );
        }

        Map<YearMonth, Double> monthlyData = history
            .stream()
            .collect(
                Collectors.groupingBy(
                    t -> YearMonth.from(t.getCreatedAt()),
                    TreeMap::new,
                    Collectors.summingDouble(t -> t.getAmount().doubleValue())
                )
            );

        PredictionDTO.Prediction bestPrediction = strategies
            .stream()
            .map(s -> tryPredict(s, monthlyData, category))
            .filter(Objects::nonNull)
            .max(Comparator.comparingDouble(PredictionDTO.Prediction::modelFit))
            .orElseThrow(() ->
                new RuntimeException(
                    "No model could generate a valid prediction"
                )
            );

        saveToDatabase(bestPrediction, category);
        return bestPrediction;
    }

    private PredictionDTO.Prediction tryPredict(
        PredictionStrategy strategy,
        Map<YearMonth, Double> monthlyData,
        TransactionCategory category
    ) {
        try {
            return strategy.predictNextMonth(monthlyData, category);
        } catch (Exception e) {
            log.warn(
                "Strategy {} failed for category {}: {}",
                strategy.getModelName(),
                category,
                e.getMessage()
            );
            return null;
        }
    }

    private void saveToDatabase(
        PredictionDTO.Prediction dto,
        TransactionCategory category
    ) {
        Prediction entity = new Prediction();
        entity.setCategory(category);
        entity.setPredictedAmount(dto.predictedAmount());
        entity.setConfidenceLevel(dto.modelFit());
        entity.setTargetDate(
            LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay()
        );

        predictionRepository.save(entity);
    }
}
