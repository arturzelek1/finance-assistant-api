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

        // Calculate the 'Drift' (average change per month)
        // Formula: (Last - First) / (Number of Intervals)
        double averageChange = (lastValue - firstValue) / (n - 1);

        // Prediction: Last observed value + 1 step of drift
        double predictedValue = lastValue + averageChange;

        // Model Fit: In drift models, we can measure how much the drift
        // actually explains the variance, but here we use a simplified confidence
        // score that rewards longer history.
        double modelFit = calculateConfidence(n);

        return PredictionMapper.mapToDto(category, Math.max(0, predictedValue), modelFit);
    }

    private double calculateConfidence(int n) {
        // Drift is more reliable over long periods
        if (n >= 12) return 0.70;
        if (n >= 6) return 0.50;
        return 0.30;
    }

    @Override
    public String getModelName() {
        return "NAIVE_DRIFT";
    }
}
