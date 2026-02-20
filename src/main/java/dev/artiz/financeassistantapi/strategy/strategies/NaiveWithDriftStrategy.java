package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.utils.PredictionValidator;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Component
public class NaiveWithDriftStrategy implements PredictionStrategy {

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        List<Double> values = PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 3);

        int n = values.size();
        double firstValue = values.getFirst();
        double lastValue = values.get(n - 1);

        // Drift calculation: represents the average change per month over the entire period.
        // Mathematical formula: (Y_t - Y_1) / (t - 1)
        double drift = (lastValue - firstValue) / (n - 1);

        // Prediction: Extending the line from the last observed value by one step of drift.
        double predictedValue = lastValue + drift;

        // Model Fit: Calculate Mean Absolute Percentage Error (MAPE) equivalent
        // to see how well the drift line represents the actual data points.
        double modelFit = calculateDriftStability(values, drift);

        return PredictionMapper.mapToDto(category, Math.max(0, predictedValue), modelFit);
    }

    private double calculateDriftStability(List<Double> values, double drift) {
        int n = values.size();
        double firstValue = values.getFirst();
        double totalError = 0;
        double totalMagnitude = 0;

        for (int i = 0; i < n; i++) {
            // Theoretical value at this step if the drift was perfectly constant
            double expectedValue = firstValue + (i * drift);
            totalError += Math.abs(values.get(i) - expectedValue);
            totalMagnitude += Math.abs(values.get(i));
        }

        // Return a score between 0 and 1
        return totalMagnitude == 0 ? 0 : Math.max(0, 1 - (totalError / totalMagnitude));
    }

    @Override
    public String getModelName() {
        return "NAIVE_DRIFT";
    }
}