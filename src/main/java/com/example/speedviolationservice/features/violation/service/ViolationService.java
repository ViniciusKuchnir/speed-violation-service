package com.example.speedviolationservice.features.violation.service;

import com.example.speedviolationservice.config.ViolationProperties;
import com.example.speedviolationservice.features.violation.model.SpeedReading;
import com.example.speedviolationservice.features.violation.model.Violation;
import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;
import com.example.speedviolationservice.features.violation.model.ViolationSeverity;
import com.example.speedviolationservice.features.violation.repository.ViolationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class ViolationService {

    private final ViolationProperties properties;
    private final ViolationRepository violationRepository;

    public ViolationService(
            ViolationProperties properties,
            ViolationRepository violationRepository
    ) {
        this.properties = properties;
        this.violationRepository = violationRepository;
    }

    public ViolationEvaluation evaluate(SpeedReading reading) {

        int consideredSpeed = this.calculateConsideredSpeed(
                reading.measuredSpeed(),
                reading.speedLimit()
        );

        BigDecimal excessPercentage = this.calculateExcessPercentage(consideredSpeed, reading.speedLimit());

        boolean hasViolation = consideredSpeed > reading.speedLimit();

        Violation violation = null;

        if (hasViolation) {
            ViolationSeverity severity = this.classifyViolation(excessPercentage);
            violation = new Violation(severity);
        }

        ViolationEvaluation evaluation =  new ViolationEvaluation(
                reading.licensePlate(),
                reading.equipmentId(),
                reading.measuredSpeed(),
                consideredSpeed,
                reading.speedLimit(),
                excessPercentage,
                hasViolation,
                violation,
                Instant.now()
        );

        if (hasViolation) {
            violationRepository.save(evaluation);
        }

        return evaluation;
    }

    public List<ViolationEvaluation> findByLicensePlate(String licensePlate) {
        return violationRepository.findByLicensePlate(licensePlate);
    }

    private int calculateConsideredSpeed(int measuredSpeed, int speedLimit) {

        if (speedLimit <= properties.percentageTreshold()) {
            return measuredSpeed - properties.fixedKmh();
        }

        BigDecimal tolerance = BigDecimal
                .valueOf(measuredSpeed)
                .multiply(BigDecimal.valueOf(properties.percentage()))
                .movePointLeft(2);

        return BigDecimal
                .valueOf(measuredSpeed)
                .subtract(tolerance)
                .intValue();
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
