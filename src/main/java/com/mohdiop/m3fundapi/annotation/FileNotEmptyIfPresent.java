package com.mohdiop.m3fundapi.annotation;

import com.mohdiop.m3fundapi.validator.FileNotEmptyIfPresentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileNotEmptyIfPresentValidator.class)
@Documented
public @interface FileNotEmptyIfPresent {
    String message() default "Le fichier ne peut pas être vide si fourni.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

