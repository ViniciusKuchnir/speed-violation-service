package com.example.speedviolationservice.features.violation.mapper;

import com.example.speedviolationservice.features.violation.dto.EvaluateViolationResponse;
import com.example.speedviolationservice.features.violation.dto.ViolationResponse;
import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;

public final class ViolationMapper {

    private ViolationMapper() {}

    public static EvaluateViolationResponse toResponse(
            ViolationEvaluation evaluation
    ) {
        ViolationResponse violationResponse = null;

        if (evaluation.violation() != null) {
            violationResponse = new ViolationResponse(
                    evaluation.violation().severity(),
                    evaluation.violation().ctbCode()
            );
        }

        return new EvaluateViolationResponse(
                evaluation.licensePlate(),
                evaluation.equipmentId(),
                evaluation.measuredSpeed(),
                evaluation.consideredSpeed(),
                evaluation.speedLimit(),
                evaluation.excessPercentage(),
                evaluation.hasViolation(),
                violationResponse,
                evaluation.processedAt()
        );
    }
}
