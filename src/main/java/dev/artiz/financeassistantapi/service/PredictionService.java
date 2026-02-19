package dev.artiz.financeassistantapi.service;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.model.Prediction;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.repository.PredictionRepository;
import dev.artiz.financeassistantapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionService {
    private final TransactionRepository repository;
    private final PredictionRepository predictionRepository;

    @Transactional
    public PredictionDTO.Prediction predictNextMonth(TransactionCategory category) {

    }



    private void savePredictionToDb(Prediction prediction) {
        prediction.setCategory(prediction.getCategory());
        prediction.setPredictedAmount(prediction.getPredictedAmount());
        prediction.setTargetDate(LocalDate.now().plusMonths(1).atStartOfDay());
        prediction.setConfidenceLevel(prediction.getModelFit());

        predictionRepository.save(prediction);
    }
}