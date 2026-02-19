package dev.artiz.financeassistantapi.strategy;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.model.TransactionCategory;

import java.util.List;

public interface PredictionStrategy {
    PredictionDTO.Prediction predictNextMonth(List<Transaction> history, TransactionCategory category);
    String getModelName();
}
