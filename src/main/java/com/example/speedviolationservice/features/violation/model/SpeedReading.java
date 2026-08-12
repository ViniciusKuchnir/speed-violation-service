package com.example.speedviolationservice.features.violation.model;

import java.time.Instant;

public record SpeedReading(
        String licensePlate,
        int measuredSpeed,
        int speedLimit,
        String equipmentId,
        Instant captureTimestamp,
        ViolationOrigin origin
) {
}
