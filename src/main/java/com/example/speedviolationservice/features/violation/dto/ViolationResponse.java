package com.example.speedviolationservice.features.violation.dto;

import com.example.speedviolationservice.features.violation.model.ViolationSeverity;

public record ViolationResponse(
        ViolationSeverity severity,
        String ctbCode
) {
}
