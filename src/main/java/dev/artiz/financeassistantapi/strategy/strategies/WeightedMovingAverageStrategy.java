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

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        List<Double> values = PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 3);

        int n = values.size();
        double weightedSum = 0;
        double weightTotal = 0;

        // Calculate the weighted average (the prediction)
        for (int i = 0; i < n; i++) {
            double weight = i + 1;
            weightedSum += values.get(i) * weight;
            weightTotal += weight;
        }
        double prediction = weightedSum / weightTotal;

        // Calculate dynamic model fit based on Weighted Variance
        double modelFit = calculateWeightedFit(values, prediction, weightTotal);

        return PredictionMapper.mapToDto(category, Math.max(0, prediction), modelFit);
    }

    private double calculateWeightedFit(List<Double> values, double prediction, double weightTotal) {
        if (prediction <= 0) return 0.0;

        double weightedSumSquaredErrors = 0;
        for (int i = 0; i < values.size(); i++) {
            double weight = i + 1;
            weightedSumSquaredErrors += weight * Math.pow(values.get(i) - prediction, 2);
        }

        double weightedVariance = weightedSumSquaredErrors / weightTotal;
        double weightedStandardDeviation = Math.sqrt(weightedVariance);

        // Coefficient of Variation approach: 1 - (StDev / Mean)
        // We cap it between 0 and 1
        double fit = 1 - (weightedStandardDeviation / prediction);
        return Math.max(0, Math.min(1, fit));
    }

    @Override
    public String getModelName() {
        return "WMA";
    }
}