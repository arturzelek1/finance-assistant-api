package dev.artiz.financeassistantapi.utils;

import dev.artiz.financeassistantapi.exception.GlobalExceptionHandler;
import dev.artiz.financeassistantapi.exception.InsufficientDataException;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class PredictionValidator {

    public static List<Double> validateAndExtractValues(Map<YearMonth, Double> monthlyData, String modelName, int minRequired) {
        if (monthlyData == null || monthlyData.size() < minRequired) {
            throw new InsufficientDataException(
                    String.format("Model %s requires at least %d months of data. Current: %d",
                            modelName, minRequired, (monthlyData == null ? 0 : monthlyData.size()))
            );
        }

        return monthlyData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }
}
