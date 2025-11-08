package com.mohdiop.m3fundapi.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class NotPastValidator implements ConstraintValidator<NotPast, LocalDateTime> {

    @Override
    public void initialize(NotPast constraintAnnotation) {
        // Pas d'initialisation nécessaire
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // La validation @NotNull gère les valeurs null
        }
        return !value.isBefore(LocalDateTime.now());
    }
}

