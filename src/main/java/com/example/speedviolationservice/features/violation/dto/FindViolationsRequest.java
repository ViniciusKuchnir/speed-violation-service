package com.example.speedviolationservice.features.violation.dto;

import com.example.speedviolationservice.features.violation.validator.ValidLicensePlate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Parameters used to query violations")
public record FindViolationsRequest(

        @Schema(
                description = "Vehicle license plate in old or Mercosul format",
                example = "ABC1D23"
        )
        @NotBlank(message = "License plate is required")
        @ValidLicensePlate
        String licensePlate
) {
}
