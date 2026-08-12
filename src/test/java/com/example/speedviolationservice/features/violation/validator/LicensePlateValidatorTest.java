package com.example.speedviolationservice.features.violation.validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class LicensePlateValidatorTest {

    private final LicensePlateValidator validator = new LicensePlateValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "ABC1234",
            "XYZ9876"
    })
    void shouldAcceptOldLicensePlateFormat(String licensePlate) {
        boolean result = validator.isValid(licensePlate, null);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ABC1D23",
            "XYZ9A87"
    })
    void shouldAcceptMercosulLicensePlateFormat(String licensePlate) {
        boolean result = validator.isValid(licensePlate, null);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ABC123",
            "ABCD1234",
            "ABC-1234",
            "ABC1D234",
            "abc1234",
            "abc1d23"
    })
    void shouldRejectInvalidLicensePlates(String licensePlate){
        assertThat(
                validator.isValid(licensePlate, null)
        ).isFalse();
    }
}
