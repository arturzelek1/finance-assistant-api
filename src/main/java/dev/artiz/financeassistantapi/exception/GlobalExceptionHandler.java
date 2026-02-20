package dev.artiz.financeassistantapi.exception;

import dev.artiz.financeassistantapi.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final AppConfig appConfig;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime Exception: ", ex);

        String message = ex.getMessage();
        if (!"dev".equals(appConfig.getEnvironment())) {
            message = "An unexpected error occurred.";
        }

        return ResponseEntity.status(500).body(Map.of("error", message));
    }

    public static class InsufficientDataException extends RuntimeException {
        public InsufficientDataException(String message) {
            super(message);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        if ("dev".equals(appConfig.getEnvironment())) {
            throw new RuntimeException("Validation Error: " + ex.getBindingResult().getAllErrors());
        }

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleEmptyBody(HttpMessageNotReadableException ex) {
        if ("dev".equals(appConfig.getEnvironment())) {
            throw new RuntimeException("JSON Parse Error: " + ex.getMostSpecificCause().getMessage());
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Malformed JSON"));
    }
}