package dev.artiz.financeassistantapi.dto;

import dev.artiz.financeassistantapi.model.TransactionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface TransactionDTO extends Serializable {
    record Create (
            @NotBlank(message = "Description cannot be empty")
            String description,
            @PositiveOrZero(message = "Amount must be positive or zero")
            BigDecimal amount,
            @NotNull(message = "Category cannot be null")
            TransactionCategory category,
            LocalDateTime createdAt
    ) implements TransactionDTO {}

    record Get (
            Long id,
            String description,
            BigDecimal amount,
            LocalDateTime createdAt
    ) implements TransactionDTO {}
}