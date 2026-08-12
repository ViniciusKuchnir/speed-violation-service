package com.example.speedviolationservice.features.violation.model;

import java.math.BigDecimal;

public record ViolationEvaluation(
        int consideredSpeed,
        BigDecimal excessPercentage,
        boolean hasViolation,
        ViolationSeverity severity
) {
}
