package com.example.speedviolationservice.features.violation.exception;

import java.time.Instant;

public record ApiError(
        String error,
        String message,
        Instant timestamp
) {
}
