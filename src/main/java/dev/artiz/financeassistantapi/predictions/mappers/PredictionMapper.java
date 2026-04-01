package dev.artiz.financeassistantapi.predictions.mappers;

import dev.artiz.financeassistantapi.predictions.dto.PredictionDTO;
import dev.artiz.financeassistantapi.predictions.model.Prediction;
import dev.artiz.financeassistantapi.transactions.model.TransactionCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PredictionMapper {

    public static PredictionDTO.Prediction mapToDto(
        TransactionCategory category,
        double predictedValue,
        Double modelFit
    ) {
        Prediction prediction = Prediction.builder()
            .predictedAmount(BigDecimal.valueOf(Math.max(0, predictedValue)))
            .modelFit(modelFit)
            .category(category)
            .targetDate(LocalDate.now().plusMonths(1).atStartOfDay())
            .build();

        return new PredictionDTO.Prediction(
            prediction.getPredictedAmount(),
            prediction.getCategory(),
            prediction.getCreatedAt(),
            prediction.getTargetDate(),
            prediction.getModelFit(),
            prediction.getConfidenceLevel()
        );
    }
}
