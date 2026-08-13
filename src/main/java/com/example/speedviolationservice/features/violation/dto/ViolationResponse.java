package com.example.speedviolationservice.features.violation.dto;

import com.example.speedviolationservice.features.violation.model.ViolationSeverity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Speed violation details")
public record ViolationResponse(

        @Schema(
                description = "Violation severity according to the excess percentage",
                example = "SERIOUS",
                allowableValues = {
                        "MEDIUM",
                        "SERIOUS",
                        "VERY_SERIOUS"
                }
        )
        ViolationSeverity severity,

        @Schema(
                description = "Corresponding Brazilian Traffic Code classification",
                example = "218-II"
        )
        String ctbCode
) {
}
