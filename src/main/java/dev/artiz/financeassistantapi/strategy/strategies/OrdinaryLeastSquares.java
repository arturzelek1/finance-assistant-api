package dev.artiz.financeassistantapi.strategy.strategies;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.strategy.PredictionStrategy;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.mappers.PredictionMapper;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class OrdinaryLeastSquares implements PredictionStrategy {
    @Override
    public PredictionDTO.Prediction predictNextMonth(List<Transaction> transactions, TransactionCategory category) {
        Map<YearMonth, Double> monthlySums = transactions.stream()
                .collect(Collectors.groupingBy(transaction ->
                        YearMonth.from(transaction.getCreatedAt()),
                        TreeMap::new,
                        Collectors.summingDouble(transaction ->
                                transaction.getAmount().doubleValue())
                ));

        List<Double> monthlyValues = new ArrayList<>(monthlySums.values());

        // A minimum of three data points is required to calculate a trend line
        if (monthlyValues.size() < 3) {
           throw new RuntimeException("Not enough data to run OLS regression");
        }

        int months = monthlyValues.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        // Accumulate statistics for the OLS estimation
        for (int i = 0; i < months; i++) {
            double y = monthlyValues.get(i);

            sumX += i;                 // Time index (independent variable)
            sumY += y;                 // Amount (dependent variable)
            sumXY += (double) i * y;   // Sum of X * Y
            sumX2 += (double) i * i;   // Sum of X squared
        }

        // Estimating beta parameters (Structural parameters of the model),
        // beta1 (slope) represents the average change per period
        double beta1 = (months * sumXY - sumX * sumY) / (months * sumX2 - sumX * sumX);
        // beta0 (intercept) represents the starting value
        double beta0 = (sumY - beta1 * sumX) / months;

        // Calculate predicted value for the next time period (n)
        double predictedValue = beta0 + beta1 * months;
        // Calculate model fit
        double rSquare = calculateRSquare(monthlyValues, beta0, beta1);

        return PredictionMapper.mapToDto(category, predictedValue, rSquare);
    }

    @Override
    public String getModelName(){ return "OLS";}

    //Calculates the R-Squared (Coefficient of Determination).
    //It compares the variance explained by the model to the total variance in the data.
    private double calculateRSquare(List<Double> monthlyValues, double beta0, double beta1) {
        double ssRes = 0; // Residual Sum of Squares (SSE)
        double ssTot = 0; // Total Sum of Squares (SST)
        double meanY = monthlyValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        for (int i = 0; i < monthlyValues.size(); i++) {
            double predicted = beta0 + beta1 * i;
            double actual = monthlyValues.get(i);

            ssRes += Math.pow(actual - predicted, 2);
            ssTot += Math.pow(actual - meanY, 2);
        }

        // Return 0 if the total variance is 0 to avoid division by zero
        return ssTot == 0 ? 0 : 1 - (ssRes / ssTot);
    }
}
