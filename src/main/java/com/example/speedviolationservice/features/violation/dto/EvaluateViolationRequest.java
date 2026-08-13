package com.example.speedviolationservice.features.violation.dto;

import com.example.speedviolationservice.features.violation.validator.ValidLicensePlate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

@Schema(description = "Vehicle speed reading to be evaluated")
public record EvaluateViolationRequest(

        @Schema(
                description = "Vehicle license plate in old or Mercosul format",
                example = "ABC1D23"
        )
        @NotBlank(message = "License plate is required")
        @ValidLicensePlate
        String licensePlate,

        @Schema(
                description = "Speed measured by the enforcement equipment in km/h",
                example = "92"
        )
        @NotNull(message = "Measured speed is required")
        @Positive(message = "Measured speed must be greater than zero")
        Integer measuredSpeed,

        @Schema(
                description = "Speed limit of the road in km/h",
                example = "60"
        )
        @NotNull(message = "Speed limit is required")
        @Positive(message = "Speed limit must be greater than zero")
        Integer speedLimit,

        @Schema(
                description = "Identifier of the enforcement equipment",
                example = "RAD-CWB-001"
        )
        @NotBlank(message = "Equipment ID is required")
        String equipmentId,

        @Schema(
                description = "Timestamp when the speed reading was captured, in ISO-8601 format",
                example = "2026-06-08T14:30:00Z"
        )
        @NotNull(message = "Capture timestamp is required")
        @PastOrPresent(message = "Capture timestamp must not be in the future")
        Instant captureTimestamp
) {
}
