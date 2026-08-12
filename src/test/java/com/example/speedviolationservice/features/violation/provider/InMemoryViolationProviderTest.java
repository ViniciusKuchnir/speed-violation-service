package com.example.speedviolationservice.features.violation.provider;

import com.example.speedviolationservice.features.violation.model.Violation;
import com.example.speedviolationservice.features.violation.model.ViolationEvaluation;
import com.example.speedviolationservice.features.violation.model.ViolationSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryViolationProviderTest {
    private InMemoryViolationProvider provider;

    @BeforeEach
    void setUp() {
        provider = new InMemoryViolationProvider();
    }

    private ViolationEvaluation createViolationEvaluation(String licensePlate) {
        return new ViolationEvaluation(
                licensePlate,
                "RAD-CWB-001",
                92,
                85,
                60,
                new BigDecimal("41.67"),
                true,
                new Violation(ViolationSeverity.SERIOUS),
                Instant.now()
        );
    }

    @Test
    void shouldSaveAndFindViolationByLicensePlate() {
        var evaluation = createViolationEvaluation("ABC1D23");

        provider.save(evaluation);

        var result = provider.findByLicensePlate("ABC1D23");

        assertThat(result)
                .hasSize(1)
                .containsExactly(evaluation);
    }

    @Test
    void shouldReturnAllViolationsForSameLicensePlate() {
        var firstViolation = createViolationEvaluation("ABC1D23");
        var secondViolation = createViolationEvaluation("ABC1D23");

        provider.save(firstViolation);
        provider.save(secondViolation);

        var result = provider.findByLicensePlate("ABC1D23");

        assertThat(result)
                .hasSize(2)
                .containsExactly(firstViolation, secondViolation);
    }

    @Test
    void shouldReturnEmptyListWhenLicensePlateHasNoViolations() {
        var result = provider.findByLicensePlate("ABC1D23");

        assertThat(result).isEmpty();
    }

}
