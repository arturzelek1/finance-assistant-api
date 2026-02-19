package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HoltWintersStrategy implements PredictionStrategy {
    // Smoothing parameters (usually between 0.1 and 0.3)
    private static final double ALPHA = 0.3;
    private static final double BETA = 0.2;

    @Override
    public PredictionDTO.Prediction predictNextMonth(List<Transaction> history, TransactionCategory category) {
        int n = history.size();
        double level = history.get(0).getAmount().doubleValue();
        double trend = history.get(1).getAmount().doubleValue() - history.get(0).getAmount().doubleValue();

        double sumSquaredErrors = 0;
        double sumTotalSquares = 0;
        double meanY = history.stream().mapToDouble(t -> t.getAmount().doubleValue()).average().orElse(0);

        for (int i = 1; i < n; i++) {
            double actual = history.get(i).getAmount().doubleValue();

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
