package com.example.speedviolationservice.features.violation.service;

import com.example.speedviolationservice.config.ViolationProperties;
import com.example.speedviolationservice.features.violation.model.ViolationSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ViolationServiceTest {

    private ViolationService service;

    @BeforeEach
    void setUp() {
        var properties = new ViolationProperties(
                7,
                7,
                100
        );

        service = new ViolationService(properties);
    }

    @Test
    void shouldUseConfiguredFixedTolerance() {

        var properties = new ViolationProperties(
                5,
                7,
                100
        );

        var customService =
                new ViolationService(properties);

        var result =
                customService.evaluate(92, 60);

        assertThat(result.consideredSpeed())
                .isEqualTo(87);
    }

    @Test
    void shouldApplyFixedToleranceWhenSpeedLimitIsAtMost100() {

        var result =  service.evaluate(92, 60);

        assertThat(result.consideredSpeed()).isEqualTo(85);
    }

    @Test
    void shouldApplyPercentageToleranceWhenSpeedLimitIsAbove100() {

        var result = service.evaluate(130, 110);

        assertThat(result.consideredSpeed()).isEqualTo(120);
    }

    @Test
    void shouldApplyFixedToleranceWhenSpeedLimitIsEqualTo100(){

        var result = service.evaluate(92, 100);

        assertThat(result.consideredSpeed()).isEqualTo(85);
    }

    @Test
    void shouldReturnZeroExcessPercentageWhenConsideredSpeedIsBelowLimit() {

        var result = service.evaluate(66, 60);

        assertThat(result.consideredSpeed()).isEqualTo(59);
        assertThat(result.excessPercentage()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldReturnZeroExcessPercentageWhenConsideredSpeedEqualsLimit() {

        var result = service.evaluate(67, 60);

        assertThat(result.consideredSpeed()).isEqualTo(60);
        assertThat(result.excessPercentage()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldCalculateExcessPercentageWhenConsideredSpeedExceedsLimit() {

        var result = service.evaluate(67, 50);

        assertThat(result.consideredSpeed()).isEqualTo(60);
        assertThat(result.excessPercentage()).isEqualByComparingTo("20.00");
    }

    @Test
    void shouldRoundExcessPercentageToTwoDecimalPlaces() {

        var result = service.evaluate(92, 60);

        assertThat(result.consideredSpeed()).isEqualTo(85);
        assertThat(result.excessPercentage()).isEqualByComparingTo("41.67");
    }

    @Test
    void shouldCalculateExcessPercentageUsingConsideredSpeed() {

        var result = service.evaluate(130, 110);

        assertThat(result.consideredSpeed()).isEqualTo(120);
        assertThat(result.excessPercentage())
                .isEqualByComparingTo("9.09");
    }

    @Test
    void shouldReturnNullSeverityWhenThereIsNoViolation() {

        var result = service.evaluate(64, 60);

        assertThat(result.hasViolation()).isFalse();
        assertThat(result.excessPercentage())
                .isEqualByComparingTo("0.00");
        assertThat(result.severity()).isNull();
    }

    @Test
    void shouldClassifyAsMediumWhenExcessIsExactly20Percent() {

        var result = service.evaluate(67, 50);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("20.00");

        assertThat(result.hasViolation()).isTrue();

        assertThat(result.severity())
                .isEqualTo(ViolationSeverity.MEDIUM);
    }

    @Test
    void shouldClassifyAsSeriousWhenExcessIsAbove20Percent() {

        var result = service.evaluate(68, 50);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("22.00");

        assertThat(result.hasViolation()).isTrue();

        assertThat(result.severity())
                .isEqualTo(ViolationSeverity.SERIOUS);
    }

    @Test
    void shouldClassifyAsSeriousWhenExcessIsExactly50Percent() {

        var result = service.evaluate(82, 50);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("50.00");

        assertThat(result.severity())
                .isEqualTo(ViolationSeverity.SERIOUS);
    }

    @Test
    void shouldClassifyAsVerySeriousWhenExcessIsAbove50Percent() {

        var result = service.evaluate(83, 50);

        assertThat(result.excessPercentage())
                .isEqualByComparingTo("52.00");

        assertThat(result.severity())
                .isEqualTo(ViolationSeverity.VERY_SERIOUS);
    }



}
