package com.mohdiop.m3fundapi.annotation;

import com.mohdiop.m3fundapi.validator.ValidCampaignValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCampaignValidator.class)
@Documented
public @interface ValidCampaign {
    String message() default "Les champs de la campagne ne sont pas valides pour le type sélectionné.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

