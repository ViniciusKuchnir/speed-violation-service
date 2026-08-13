package com.example.speedviolationservice.features.violation.dto;

import com.example.speedviolationservice.features.violation.validator.ValidLicensePlate;
import jakarta.validation.constraints.NotBlank;

public record FindViolationsRequest(
        @NotBlank(message = "License plate is required")
        @ValidLicensePlate
        String licensePlate
) {
}
