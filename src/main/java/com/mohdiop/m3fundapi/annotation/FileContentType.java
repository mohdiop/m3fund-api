package com.mohdiop.m3fundapi.annotation;

import com.mohdiop.m3fundapi.validator.FileContentTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileContentTypeValidator.class)
@Documented
public @interface FileContentType {
    String[] allowed(); // types MIME autorisés
    String message() default "Type de fichier non autorisé.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

