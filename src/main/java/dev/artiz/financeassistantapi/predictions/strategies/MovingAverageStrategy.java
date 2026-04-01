package dev.artiz.financeassistantapi.predictions.strategies;

import dev.artiz.financeassistantapi.predictions.PredictionStrategy;
import dev.artiz.financeassistantapi.predictions.dto.PredictionDTO;
import dev.artiz.financeassistantapi.predictions.mappers.PredictionMapper;
import dev.artiz.financeassistantapi.predictions.utils.PredictionValidator;
import dev.artiz.financeassistantapi.transactions.model.TransactionCategory;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MovingAverageStrategy implements PredictionStrategy {

    private static final int WINDOW_SIZE = 3;

    @Override
    public PredictionDTO.Prediction predictNextMonth(
        Map<YearMonth, Double> monthlyData,
        TransactionCategory category
    ) {
        List<Double> values = PredictionValidator.validateAndExtractValues(
            monthlyData,
            getModelName(),
            WINDOW_SIZE
        );

        // We only care about the last N months to capture the current spending trend
        List<Double> recentValues = values
            .stream()
            .skip(Math.max(0, values.size() - WINDOW_SIZE))
            .collect(Collectors.toList());

        // Calculate the Simple Moving Average (SMA)
        double avg = recentValues
            .stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        // Calculate modelFit based on the Coefficient of Variation (CV)
        // High stability in the window = high model fit
        double standardDeviation = calculateStandardDeviation(
            recentValues,
            avg
        );

        // modelFit = 1 - (Relative Standard Deviation)
        // This quantifies how much the recent spending fluctuates around the mean
        double modelFit =
            avg == 0 ? 0 : Math.max(0, 1 - (standardDeviation / avg));

        return PredictionMapper.mapToDto(category, avg, modelFit);
    }

    private double calculateStandardDeviation(List<Double> values, double avg) {
        double sumSquaredErrors = values
            .stream()
            .mapToDouble(actual -> Math.pow(actual - avg, 2))
            .sum();

        return Math.sqrt(sumSquaredErrors / values.size());
    }

    @Override
    public String getModelName() {
        return "MOVING_AVERAGE";
    }
}
