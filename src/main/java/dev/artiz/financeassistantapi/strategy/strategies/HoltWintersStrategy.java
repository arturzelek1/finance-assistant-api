package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import dev.artiz.financeassistantapi.utils.PredictionValidator;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Component
public class HoltWintersStrategy implements PredictionStrategy {
    // Smoothing parameters (usually between 0.1 and 0.3)
    private static final double ALPHA = 0.3;
    private static final double BETA = 0.2;

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        List<Double> values = PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 3);

        int n = values.size();
        double level = values.get(0);
        double trend = values.get(1) - values.get(0);

        double sumSquaredErrors = 0;
        double sumTotalSquares = 0;
        double meanY = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        for (int i = 1; i < n; i++) {
            double actual = values.get(i);

            // Before updating level/trend, we see what the model "guessed" for this step
            double forecastForThisStep = level + trend;

            // Update model parameters (Recursive part)
            double lastLevel = level;
            level = ALPHA * actual + (1 - ALPHA) * (level + trend);
            trend = BETA * (level - lastLevel) + (1 - BETA) * trend;

            // Calculate errors for Model Fit (R-Squared equivalent)
            sumSquaredErrors += Math.pow(actual - forecastForThisStep, 2);
            sumTotalSquares += Math.pow(actual - meanY, 2);
        }

        double predictedValue = level + trend;

        // Calculate model fit for Holt-Winters
        double modelFit = sumTotalSquares == 0 ? 0 : 1 - (sumSquaredErrors / sumTotalSquares);

        return PredictionMapper.mapToDto(category, predictedValue, modelFit);
    }

    @Override
    public String getModelName() {
        return "HOLT_WINTERS";
    }
}
