package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Map;

@Slf4j
@Component
public class SeasonalPersistenceStrategy implements PredictionStrategy {

    @Override
    public PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category) {

        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        YearMonth targetMonthLastYear = nextMonth.minusYears(1);

        if (!monthlyData.containsKey(targetMonthLastYear)) {
            throw new RuntimeException("Missing historical data for seasonal reference: " + targetMonthLastYear);
        }
        double predictedValue = monthlyData.getOrDefault(targetMonthLastYear, 0.0);

        // Calculate model fit based on data depth
        // More historical years provide higher confidence for seasonal patterns
        double modelFit = calculateConfidence(monthlyData.size());

        log.debug("Seasonal Strategy: Predicted value for {} based on {} data: {}",
                nextMonth, targetMonthLastYear, predictedValue);

        return PredictionMapper.mapToDto(category, predictedValue, modelFit);
    }

    private double calculateConfidence(int totalMonths) {
        if (totalMonths >= 24) return 0.85; // Strong evidence (2+ years)
        if (totalMonths >= 13) return 0.65; // Minimum requirement met
        return 0.0;
    }

    @Override
    public String getModelName() {
        return "SEASONAL_PERSISTENCE";
    }
}