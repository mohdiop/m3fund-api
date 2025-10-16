package com.mohdiop.m3fundapi.annotation;

import com.mohdiop.m3fundapi.validator.ValidFileListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileListValidator.class)
@Documented
public @interface ValidFileList {
    String[] allowed();
    String message() default "Fichiers non valides.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

