package com.example.speedviolationservice.features.violation.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ViolationEvaluation(
        String licensePlate,
        String equipmentId,
        int measuredSpeed,
        int consideredSpeed,
        int speedLimit,
        BigDecimal excessPercentage,
        boolean hasViolation,
        Violation violation,
        Instant processedAt
) {
}
