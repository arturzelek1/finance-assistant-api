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
public class WeightedMovingAverageStrategy implements PredictionStrategy {

    private static final int WINDOW_SIZE = 6;

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        List<Double> values = PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 3);

        // We take the last N values to focus on the current financial behavior.
        List<Double> windowedValues = values.size() > WINDOW_SIZE
                ? values.subList(values.size() - WINDOW_SIZE, values.size())
                : values;

        int n = windowedValues.size();
        double weightedSum = 0;
        double weightTotal = 0;

        // Calculate WMA: Weights increase linearly (1, 2, 3... n)
        // Recent data has the highest impact (weight n).
        for (int i = 0; i < n; i++) {
            double weight = i + 1;
            weightedSum += windowedValues.get(i) * weight;
            weightTotal += weight;
        }

        double prediction = weightedSum / weightTotal;

        // Calculate model fit using weighted variance.
        // It measures how consistently the data follows the weighted mean.
        double modelFit = calculateWeightedFit(windowedValues, prediction, weightTotal);

        return PredictionMapper.mapToDto(category, Math.max(0, prediction), modelFit);
    }

    private double calculateWeightedFit(List<Double> values, double prediction, double weightTotal) {
        if (prediction <= 0) return 0.0;

        double weightedSumSquaredErrors = 0;
        for (int i = 0; i < values.size(); i++) {
            double weight = i + 1;
            weightedSumSquaredErrors += weight * Math.pow(values.get(i) - prediction, 2);
        }

        // Weighted Variance = (sum of w_i * (y_i - y_hat)^2) / sum of weights
        double weightedVariance = weightedSumSquaredErrors / weightTotal;
        double weightedStandardDeviation = Math.sqrt(weightedVariance);

        // Confidence score: 1 - CV (Coefficient of Variation)
        double fit = 1 - (weightedStandardDeviation / prediction);
        return Math.max(0, Math.min(1, fit));
    }

    @Override
    public String getModelName() {
        return "WMA";
    }
}