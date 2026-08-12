package com.example.speedviolationservice.features.violation.exception;

import com.example.speedviolationservice.features.violation.dto.EvaluateViolationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        var fieldError = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .orElse(null);

        String field = fieldError != null
                ? fieldError.getField()
                : null;

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : "Invalid request";

        String error = resolveValidationErrorCode(field);

        Instant timestamp = Instant.now();

        EvaluateViolationRequest request =
                extractRequest(exception);

        LOGGER.warn(
                "Validation error: type={}, field={}, licensePlate={}, equipmentId={}, timestamp={}",
                error,
                field,
                request != null ? request.licensePlate() : null,
                request != null ? request.equipmentId() : null,
                timestamp
        );

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        error,
                        message,
                        timestamp
                ));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingRequestHeader(
            MissingRequestHeaderException exception
    ) {
        Instant timestamp = Instant.now();

        LOGGER.warn(
                "Missing request header: header={}, type={}, timestamp={}",
                exception.getHeaderName(),
                "INVALID_X_ORIGIN",
                timestamp
        );

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        "INVALID_X_ORIGIN",
                        "x-origin header is required",
                        timestamp
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        Instant timestamp = Instant.now();

        LOGGER.warn(
                "Invalid request value: parameter={}, value={}, type={}, timestamp={}",
                exception.getName(),
                exception.getValue(),
                "INVALID_X_ORIGIN",
                timestamp
        );

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        "INVALID_X_ORIGIN",
                        "x-origin must be one of FIXED, MOBILE or HANDHELD",
                        timestamp
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMessageNotReadable(
            HttpMessageNotReadableException exception
    ) {
        Instant timestamp = Instant.now();

        LOGGER.warn(
                "Invalid request body: type={}, timestamp={}",
                "INVALID_REQUEST",
                timestamp
        );

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        "INVALID_REQUEST",
                        "Invalid request body or field format",
                        timestamp
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception
    ) {
        Instant timestamp = Instant.now();

        LOGGER.error(
                "Unexpected error: type={}, timestamp={}",
                exception.getClass().getSimpleName(),
                timestamp,
                exception
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred",
                        timestamp
                ));
    }

    private String resolveValidationErrorCode(String field) {
        if (field == null) {
            return "VALIDATION_ERROR";
        }

        return switch (field) {
            case "licensePlate" ->
                    "INVALID_LICENSE_PLATE";
            case "measuredSpeed" ->
                    "INVALID_MEASURED_SPEED";
            case "speedLimit" ->
                    "INVALID_SPEED_LIMIT";
            case "equipmentId" ->
                    "INVALID_EQUIPMENT_ID";
            case "captureTimestamp" ->
                    "INVALID_CAPTURE_TIMESTAMP";
            default ->
                    "VALIDATION_ERROR";
        };
    }

    private EvaluateViolationRequest extractRequest(
            MethodArgumentNotValidException exception
    ) {
        Object target = exception
                .getBindingResult()
                .getTarget();

        if (target instanceof EvaluateViolationRequest request) {
            return request;
        }

        return null;
    }
}
