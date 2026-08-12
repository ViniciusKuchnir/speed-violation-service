package com.example.speedviolationservice.features.violation.service;

import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;
import com.example.speedviolationservice.features.violation.model.ViolationSeverity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ViolationService {

    public ViolationEvaluation evaluate(
            int measuredSpeed,
            int speedLimit
    ) {

        int consideredSpeed = this.calculateConsideredSpeed(measuredSpeed, speedLimit);

        BigDecimal excessPercentage = this.calculateExcessPercentage(consideredSpeed, speedLimit);

        boolean hasViolation = consideredSpeed > speedLimit;

        ViolationSeverity severity = null;

        if (hasViolation) {
            severity = this.classifyViolation(excessPercentage);
        }

        return new ViolationEvaluation(
                consideredSpeed,
                excessPercentage,
                hasViolation,
                severity
        );
    }

    private int calculateConsideredSpeed(int measuredSpeed, int speedLimit) {
        int consideredSpeed;

        if (speedLimit <= 100) {
            consideredSpeed = measuredSpeed - 7;
        }else {
            consideredSpeed = (int) (measuredSpeed * 0.93); // 0.93 = -7%
        }

        return consideredSpeed;
    }

    private BigDecimal calculateExcessPercentage(
            int consideredSpeed,
            int speedLimit
    ) {
        if (consideredSpeed <= speedLimit){
            return BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        }

        BigDecimal excess = BigDecimal.valueOf(consideredSpeed - speedLimit);
        BigDecimal limit = BigDecimal.valueOf(speedLimit);

        return excess
                .divide(limit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private ViolationSeverity classifyViolation(BigDecimal excessPercentage) {
        if (excessPercentage.compareTo(BigDecimal.valueOf(20)) <= 0) {
            return ViolationSeverity.MEDIUM;
        }

        if (excessPercentage.compareTo(BigDecimal.valueOf(50)) <= 0) {
            return ViolationSeverity.SERIOUS;
        }

        return ViolationSeverity.VERY_SERIOUS;
    }

}
