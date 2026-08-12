package com.example.speedviolationservice.features.violation.dto;

import com.example.speedviolationservice.features.violation.validator.ValidLicensePlate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record EvaluateViolationRequest(
        @NotBlank(message = "License plate is required")
        @ValidLicensePlate
        String licensePlate,

        @NotNull(message = "Measured speed is required")
        @Positive(message = "Measured speed must be greater than zero")
        Integer measuredSpeed,

        @NotNull(message = "Speed limit is required")
        @Positive(message = "Speed limit must be greater than zero")
        Integer speedLimit,

        @NotBlank(message = "Equipment ID is required")
        String equipmentId,

        @NotNull(message = "Capture timestamp is required")
        @PastOrPresent(message = "Capture timestamp must not be in the future")
        Instant captureTimestamp
) {
}
