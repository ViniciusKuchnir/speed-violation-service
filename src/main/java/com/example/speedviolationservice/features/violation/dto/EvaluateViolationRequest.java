package com.example.speedviolationservice.features.violation.dto;

import java.time.Instant;

public record EvaluateViolationRequest(
        String licensePlate,
        Integer measuredSpeed,
        Integer speedLimit,
        String equipmentId,
        Instant captureTimestamp
) {
}
