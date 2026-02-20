package dev.artiz.financeassistantapi.strategy;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.model.TransactionCategory;

import java.time.YearMonth;
import java.util.Map;

public interface PredictionStrategy {
    PredictionDTO.Prediction predictNextMonth(Map<YearMonth, Double> monthlyData, TransactionCategory category);
    String getModelName();
}
