package com.example.speedviolationservice.features.violation.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standardized API error response")
public record ApiError(

        @Schema(
                description = "Machine-readable error code",
                example = "INVALID_LICENSE_PLATE"
        )
        String error,

        @Schema(
                description = "Human-readable error description",
                example = "Invalid license plate format"
        )
        String message,

        @Schema(
                description = "Timestamp when the error occurred",
                example = "2026-08-12T22:00:00Z"
        )
        Instant timestamp
) {
}
