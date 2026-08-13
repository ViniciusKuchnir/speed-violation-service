package com.example.speedviolationservice.features.violation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Result of a speed violation evaluation")
public record EvaluateViolationResponse(

        @Schema(
                description = "Vehicle license plate",
                example = "ABC1D23"
        )
        String licensePlate,

        @Schema(
                description = "Identifier of the enforcement equipment",
                example = "RAD-CWB-001"
        )
        String equipmentId,

        @Schema(
                description = "Speed originally measured in km/h",
                example = "92"
        )
        int measuredSpeed,

        @Schema(
                description = "Speed considered after applying the tolerance rule",
                example = "85"
        )
        int consideredSpeed,

        @Schema(
                description = "Speed limit of the road in km/h",
                example = "60"
        )
        int speedLimit,

        @Schema(
                description = "Percentage by which the considered speed exceeds the limit",
                example = "41.67"
        )
        BigDecimal excessPercentage,

        @Schema(
                description = "Indicates whether the evaluated reading resulted in a violation",
                example = "true"
        )
        boolean hasViolation,

        @Schema(
                description = "Violation details when a violation is detected",
                nullable = true
        )
        ViolationResponse violation,

        @Schema(
                description = "Timestamp when the evaluation was processed",
                example = "2026-06-08T14:30:05Z"
        )
        Instant processedAt
) {
}
