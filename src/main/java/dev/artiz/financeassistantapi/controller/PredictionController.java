package dev.artiz.financeassistantapi.controller;

import dev.artiz.financeassistantapi.dto.PredictionDTO;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.model.TransactionCategory;
import dev.artiz.financeassistantapi.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
public class PredictionController {
    private final PredictionService predictionService;

    @PostMapping("/next-month")
    public ResponseEntity<PredictionDTO.Prediction> predictNextMonth(@Valid @RequestBody PredictionDTO.PredictionRequest request) {
        return ResponseEntity.ok(predictionService.predictNextMonth(request.category()));
    }
}
