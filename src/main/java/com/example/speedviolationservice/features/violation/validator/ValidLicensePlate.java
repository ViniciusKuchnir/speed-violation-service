package com.example.speedviolationservice.features.violation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LicensePlateValidator.class)
public @interface ValidLicensePlate {
    String message() default "Invalid license plate format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
