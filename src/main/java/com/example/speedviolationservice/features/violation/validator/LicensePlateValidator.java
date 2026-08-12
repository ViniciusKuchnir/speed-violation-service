package com.example.speedviolationservice.features.violation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class LicensePlateValidator implements ConstraintValidator<ValidLicensePlate, String> {

    private static final Pattern OLD_PLATE_PATTERN =
            Pattern.compile("^[A-Z]{3}[0-9]{4}$");

    private static final Pattern MERCOSUL_PLATE_PATTERN =
            Pattern.compile("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");


    @Override
    public boolean isValid(String licensePlate, ConstraintValidatorContext context) {
        if (licensePlate == null || licensePlate.isBlank()) {
            return true;
        }

        return OLD_PLATE_PATTERN.matcher(licensePlate).matches()
                || MERCOSUL_PLATE_PATTERN.matcher(licensePlate).matches();

    }
}
