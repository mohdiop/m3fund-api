package com.mohdiop.m3fundapi.annotation;

import com.mohdiop.m3fundapi.validator.FileNotEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileNotEmptyValidator.class)
@Documented
public @interface FileNotEmpty {
    String message() default "Le fichier ne peut pas être vide.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

