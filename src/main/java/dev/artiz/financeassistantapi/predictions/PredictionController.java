package dev.artiz.financeassistantapi.predictions;

import dev.artiz.financeassistantapi.predictions.dto.PredictionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping("/next-month")
    public Mono<ResponseEntity<PredictionDTO.Prediction>> predictNextMonth(
        @Valid @RequestBody PredictionDTO.PredictionRequest request
    ) {
        return predictionService
            .predictNextMonth(request.category())
            .map(ResponseEntity::ok);
    }
}
