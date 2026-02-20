package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Collection;
import java.util.Map;

@Component
public class MovingAverageStrategy implements PredictionStrategy {

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        if (monthlyData == null || monthlyData.isEmpty()) {
            throw new IllegalArgumentException("History cannot be empty");
        }

        Collection<Double> values = monthlyData.values();

        // Calculate the average (forecast)
        double avg = values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // Calculate modelFit based on the Coefficient of Variation
        // It indicates data stability: the lower the deviation, the higher the fit.
        double standardDeviation = calculateStandardDeviation(values, avg);

        // modelFit = 1 - (Relative Standard Deviation)
        // High variance in spending results in a lower confidence score.
        double modelFit = avg == 0 ? 0 : Math.max(0, 1 - (standardDeviation / avg));

        return PredictionMapper.mapToDto(category, avg, modelFit);
    }

    private double calculateStandardDeviation(Collection<Double> values, double avg) {
        double sumSquaredErrors = 0;

        // Sum of squares of deviations from the mean
        for (Double actual : values) {
            sumSquaredErrors += Math.pow(actual - avg, 2);
        }

        double variance = sumSquaredErrors / values.size();
        return Math.sqrt(variance);
    }

    @Override
    public String getModelName() {
        return "MovingAverage";
    }
}