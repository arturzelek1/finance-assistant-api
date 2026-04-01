package dev.artiz.financeassistantapi.predictions;

import dev.artiz.financeassistantapi.predictions.dto.PredictionDTO;
import dev.artiz.financeassistantapi.transactions.model.TransactionCategory;
import java.time.YearMonth;
import java.util.Map;

public interface PredictionStrategy {
    PredictionDTO.Prediction predictNextMonth(
        Map<YearMonth, Double> monthlyData,
        TransactionCategory category
    );
    String getModelName();
}
