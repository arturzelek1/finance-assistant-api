package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovingAverageStrategy implements PredictionStrategy {

    @Override
    public PredictionDTO.Prediction predictNextMonth(List<Transaction> history, TransactionCategory category) {
        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException("History cannot be empty");
        }

        // Calculate the average (forecast)
        double avg = history.stream()
                .mapToDouble(t -> t.getAmount().doubleValue())
                .average()
                .orElse(0.0);

        // Calculate modelFit based on the Coefficient of Variation
        // It indicates data stability: the lower the deviation, the higher the fit.
        double standardDeviation = getStandardDeviation(history, avg);

        // modelFit = 1 - (Relative Standard Deviation)
        // High variance in spending results in a lower confidence score.
        double modelFit = avg == 0 ? 0 : Math.max(0, 1 - (standardDeviation / avg));

        return PredictionMapper.mapToDto(category, avg, modelFit);
    }

    private static double getStandardDeviation(List<Transaction> history, double avg) {
        double sumSquaredErrors = 0;

        // Sum of squares of deviations from the mean
        for (Transaction t : history) {
            double actual = t.getAmount().doubleValue();
            sumSquaredErrors += Math.pow(actual - avg, 2);
        }

        double variance = sumSquaredErrors / history.size();
        return Math.sqrt(variance);
    }

    @Override
    public String getModelName() {
        return "MovingAverage";
    }
}