package dev.artiz.financeassistantapi.exception;

import dev.artiz.financeassistantapi.config.AppConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final AppConfig appConfig;


    @ExceptionHandler(InsufficientDataException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientData(InsufficientDataException ex) {
        log.warn("Prediction failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaught(Exception ex) {
        log.error("Unhandled exception: ", ex);

        String message = "dev".equals(appConfig.getEnvironment())
                ? ex.getMessage()
                : "An unexpected error occurred.";

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, Map<String, String> details) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .details(details)
                .build();
        return new ResponseEntity<>(response, status);
    }

    @Getter
    @Builder
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> details;
    }
}