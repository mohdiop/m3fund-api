package com.mohdiop.m3fundapi.annotation;
import com.mohdiop.m3fundapi.validator.FileContentTypeIfPresentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileContentTypeIfPresentValidator.class)
@Documented
public @interface FileContentTypeIfPresent {
    String[] allowed();
    String message() default "Type de fichier non autorisé.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

