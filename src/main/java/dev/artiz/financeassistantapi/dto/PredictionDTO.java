package dev.artiz.financeassistantapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface PredictionDTO {
    record Prediction(
            @NotNull(message = "Predicted amount cannot be null")
            BigDecimal predictedAmount,

            @NotNull(message = "Category cannot be null")
            TransactionCategory category,

            @NotNull(message = "Target month cannot be null")
            @JsonFormat(pattern = "yyyy-MM")
            LocalDateTime targetMonth,

            LocalDateTime createdAt,
            Double modelFit,
            Double confidenceLevel
    ) implements PredictionDTO {}

    record PredictionRequest(
            @NotNull(message = "Category cannot be null")
            TransactionCategory category
    ) {}
}
