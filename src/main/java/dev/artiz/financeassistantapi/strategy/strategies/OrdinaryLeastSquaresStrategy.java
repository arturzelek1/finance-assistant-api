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
public class OrdinaryLeastSquaresStrategy implements PredictionStrategy {
    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        List<Double> values = PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 3);

        int n = values.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        // Accumulate statistics for the OLS estimation
        for (int i = 0; i < n; i++) {
            double y = values.get(i);
            sumX += i;                 // Independent variable (Time index)
            sumY += y;                 // Dependent variable (Amount)
            sumXY += (double) i * y;
            sumX2 += (double) i * i;
        }

        // Calculate the slope (beta1) - represents the average change per month
        // Formula: [n*sum(xy) - sum(x)*sum(y)] / [n*sum(x^2) - (sum(x))^2]
        double denominator = (n * sumX2 - sumX * sumX);
        double beta1 = (denominator == 0) ? 0 : (n * sumXY - sumX * sumY) / denominator;

        // Calculate the intercept (beta0) - represents the estimated starting value
        // Formula: y_mean - beta1 * x_mean
        double beta0 = (sumY - beta1 * sumX) / n;

        // Forecast for the next period (index n)
        // Y_hat = beta0 + beta1 * n
        double predictedValue = beta0 + beta1 * n;

        // R-Squared (Coefficient of Determination)
        // Quantifies how much of the variance is explained by the linear trend.
        double rSquare = calculateRSquare(values, beta0, beta1);

        return PredictionMapper.mapToDto(category, Math.max(0, predictedValue), Math.max(0, rSquare));
    }

    private double calculateRSquare(List<Double> monthlyValues, double beta0, double beta1) {
        double ssRes = 0; // Residual Sum of Squares (variation not explained by the model)
        double ssTot = 0; // Total Sum of Squares (total variation in the data)
        double meanY = monthlyValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        for (int i = 0; i < monthlyValues.size(); i++) {
            double predicted = beta0 + beta1 * i;
            double actual = monthlyValues.get(i);

            ssRes += Math.pow(actual - predicted, 2);
            ssTot += Math.pow(actual - meanY, 2);
        }

        return ssTot == 0 ? 0 : 1 - (ssRes / ssTot);
    }

    @Override
    public String getModelName() {
        return "OLS";
    }
}