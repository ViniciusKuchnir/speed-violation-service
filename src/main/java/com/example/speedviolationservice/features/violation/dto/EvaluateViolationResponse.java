package com.example.speedviolationservice.features.violation.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record EvaluateViolationResponse(
        String licensePlate,
        String equipmentId,
        int measuredSpeed,
        int consideredSpeed,
        int speedLimit,
        BigDecimal excessPercentage,
        boolean hasViolation,
        ViolationResponse violation,
        Instant processedAt
) {
}
