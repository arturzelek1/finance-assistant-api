package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.utils.PredictionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Map;

@Slf4j
@Component
public class SeasonalPersistenceStrategy implements PredictionStrategy {

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {
        PredictionValidator.validateAndExtractValues(monthlyData, getModelName(), 1);

        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        YearMonth targetMonthLastYear = nextMonth.minusYears(1);

        // Check if the specific seasonal reference point exists
        if (!monthlyData.containsKey(targetMonthLastYear)) {
            log.warn("Strategy {} failed for category {}: Missing historical data for seasonal reference: {}",
                    getModelName(), category, targetMonthLastYear);
            // In a Master's project, returning 0 fit/confidence is better than crashing
            return PredictionMapper.mapToDto(category, 0.0, 0.0);
        }

        double predictedValue = monthlyData.get(targetMonthLastYear);

        // Calculate model fit based on overall data variance and depth
        // A seasonal model is more reliable when the historical data isn't chaotic
        double modelFit = calculateSeasonalConfidence(monthlyData, predictedValue);

        log.debug("Seasonal Prediction for {}: {} (based on {})", nextMonth, predictedValue, targetMonthLastYear);

        return PredictionMapper.mapToDto(category, predictedValue, modelFit);
    }

    /**
     * Estimates confidence based on data depth.
     * Seasonal patterns require at least 13 months to even exist,
     * and 24+ months to be verified across multiple cycles.
     */
    private double calculateSeasonalConfidence(Map<YearMonth, Double> monthlyData, double predictedValue) {
        int n = monthlyData.size();
        if (n < 12) return 0.0;

        // Base confidence starts higher if we have more years of history
        double baseConfidence = n >= 24 ? 0.80 : 0.50;

        // If the predicted seasonal value is an extreme outlier (e.g., 0), lower the confidence
        if (predictedValue <= 0) return baseConfidence * 0.5;

        return baseConfidence;
    }

    @Override
    public String getModelName() {
        return "SEASONAL_PERSISTENCE";
    }
}