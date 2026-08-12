package com.example.speedviolationservice.features.violation.service;

import com.example.speedviolationservice.config.ViolationProperties;
import com.example.speedviolationservice.features.violation.model.SpeedReading;
import com.example.speedviolationservice.features.violation.model.ViolationOrigin;
import com.example.speedviolationservice.features.violation.model.ViolationSeverity;
import com.example.speedviolationservice.features.violation.repository.ViolationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ViolationServiceTest {

    private ViolationService service;
    private ViolationRepository repository;

    private SpeedReading createReading(
            int measuredSpeed,
            int speedLimit
    ) {
        return new SpeedReading(
                "ABC1D23",
                measuredSpeed,
                speedLimit,
                "RAD-CWB-001",
                Instant.parse("2026-06-08T14:30:00Z"),
                ViolationOrigin.FIXED
        );
    }

    @BeforeEach
    void setUp() {
        var properties = new ViolationProperties(
                7,
                7,
                100
        );

        repository = mock(ViolationRepository.class);

        service = new ViolationService(properties, repository);
    }

    @Test
    void shouldUseConfiguredFixedTolerance() {

        var properties = new ViolationProperties(
                5,
                7,
                100
        );

        repository = mock(ViolationRepository.class);

        var customService =
                new ViolationService(properties, repository);

        var reading = createReading(92, 60);

        var result =
                customService.evaluate(reading);

        assertThat(result.consideredSpeed())
                .isEqualTo(87);
    }

    @Test
    void shouldApplyFixedToleranceWhenSpeedLimitIsAtMost100() {

        var reading = createReading(92, 60);

        var result =  service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(85);
    }

    @Test
    void shouldApplyPercentageToleranceWhenSpeedLimitIsAbove100() {

        var reading = createReading(130, 110);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(120);
    }

    @Test
    void shouldApplyFixedToleranceWhenSpeedLimitIsEqualTo100(){

        var reading = createReading(92, 100);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(85);
    }

    @Test
    void shouldReturnZeroExcessPercentageWhenConsideredSpeedIsBelowLimit() {

        var reading = createReading(66, 60);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(59);
        assertThat(result.excessPercentage()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldReturnZeroExcessPercentageWhenConsideredSpeedEqualsLimit() {

        var reading = createReading(67, 60);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(60);
        assertThat(result.excessPercentage()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldCalculateExcessPercentageWhenConsideredSpeedExceedsLimit() {

        var reading = createReading(67, 50);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(60);
        assertThat(result.excessPercentage()).isEqualByComparingTo("20.00");
    }

    @Test
    void shouldRoundExcessPercentageToTwoDecimalPlaces() {

        var reading = createReading(92, 60);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(85);
        assertThat(result.excessPercentage()).isEqualByComparingTo("41.67");
    }

    @Test
    void shouldCalculateExcessPercentageUsingConsideredSpeed() {

        var reading = createReading(130, 110);

        var result = service.evaluate(reading);

        assertThat(result.consideredSpeed()).isEqualTo(120);
        assertThat(result.excessPercentage())
                .isEqualByComparingTo("9.09");
    }

    @Test
    void shouldReturnNullSeverityWhenThereIsNoViolation() {

        var reading = createReading(64, 60);

        var result = service.evaluate(reading);

        assertThat(result.hasViolation()).isFalse();

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("0.00");

        assertThat(result.violation()).isNull();
    }

    @Test
    void shouldClassifyAsMediumWhenExcessIsExactly20Percent() {

        var reading = createReading(67, 50);

        var result = service.evaluate(reading);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("20.00");

        assertThat(result.hasViolation()).isTrue();

        assertThat(result.violation().severity())
                .isEqualTo(ViolationSeverity.MEDIUM);

        assertThat(result.violation().ctbCode())
                .isEqualTo("218-I");
    }

    @Test
    void shouldClassifyAsSeriousWhenExcessIsAbove20Percent() {

        var reading = createReading(68, 50);

        var result = service.evaluate(reading);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("22.00");

        assertThat(result.hasViolation()).isTrue();

        assertThat(result.violation().severity())
                .isEqualTo(ViolationSeverity.SERIOUS);

        assertThat(result.violation().ctbCode())
                .isEqualTo("218-II");
    }

    @Test
    void shouldClassifyAsSeriousWhenExcessIsExactly50Percent() {

        var reading = createReading(82, 50);

        var result = service.evaluate(reading);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("50.00");

        assertThat(result.hasViolation()).isTrue();

        assertThat(result.violation().severity())
                .isEqualTo(ViolationSeverity.SERIOUS);

        assertThat(result.violation().ctbCode())
                .isEqualTo("218-II");
    }

    @Test
    void shouldClassifyAsVerySeriousWhenExcessIsAbove50Percent() {

        var reading = createReading(83, 50);

        var result = service.evaluate(reading);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("52.00");

        assertThat(result.hasViolation()).isTrue();

        assertThat(result.violation().severity())
                .isEqualTo(ViolationSeverity.VERY_SERIOUS);

        assertThat(result.violation().ctbCode())
                .isEqualTo("218-III");
    }

    @Test
    void shouldPersistEvaluationWhenViolationOccurs() {
        var reading = createReading(92, 60);

        var result = service.evaluate(reading);

        verify(repository).save(result);
    }

    @Test
    void shouldNotPersistEvaluationWhenThereIsNoViolation() {
        var reading = createReading(64, 60);

        service.evaluate(reading);

        verify(repository, never()).save(any());
    }

}
