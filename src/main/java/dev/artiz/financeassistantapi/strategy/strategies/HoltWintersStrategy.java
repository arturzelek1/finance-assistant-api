package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import dev.artiz.financeassistantapi.utils.PredictionValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Component
public class HoltWintersStrategy implements PredictionStrategy {

    @Value("${forecasting.holt-winters.alpha:0.3}")
    private double alpha;

    @Value("${forecasting.holt-winters.beta:0.2}")
    private double beta;

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        // Validation: Holt-Winters requires at least 3 data points to initialize level and trend components.
        List<Double> values = PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 3);

        int n = values.size();

        // Initializing components:
        // Level (S) starts at the first value.
        // Trend (B) starts as the difference between the second and first month.
        double level = values.get(0);
        double trend = values.get(1) - values.get(0);

        // Statistics for calculating the Coefficient of Determination (R-squared)
        double sumSquaredErrors = 0;
        double meanY = values.stream().mapToDouble(v -> v).average().orElse(0);
        double sumTotalSquares = values.stream().mapToDouble(v -> Math.pow(v - meanY, 2)).sum();

        // Recursive smoothing process (Holt's Linear Trend Algorithm)
        for (int i = 1; i < n; i++) {
            double actual = values.get(i);

            // Point prediction for the current step (used to evaluate historical model fit)
            double forecastForThisStep = level + trend;

            // Updating model components based on forecast error
            double lastLevel = level;
            level = alpha * actual + (1 - alpha) * (level + trend);
            trend = beta * (level - lastLevel) + (1 - beta) * trend;

            // Cumulative Sum of Squared Errors (SSE)
            sumSquaredErrors += Math.pow(actual - forecastForThisStep, 2);
        }

        // Forecast for the next period (h=1): F(t+h) = Level(t) + h * Trend(t)
        // Math.max ensures domain integrity; financial expenses cannot be negative.
        double predictedValue = Math.max(0, level + trend);

        // Model Fit Calculation (R-squared equivalent).
        // A value of 1.0 indicates a perfect fit with historical data.
        double modelFit = sumTotalSquares == 0 ? 0 : Math.max(0, 1 - (sumSquaredErrors / sumTotalSquares));

        return PredictionMapper.mapToDto(category, predictedValue, modelFit);
    }

    @Override
    public String getModelName() {
        return "HOLT_WINTERS";
    }
}