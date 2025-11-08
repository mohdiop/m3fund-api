package com.mohdiop.m3fundapi.validator;

import com.mohdiop.m3fundapi.annotation.ValidCampaign;
import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCampaignValidator implements ConstraintValidator<ValidCampaign, CreateCampaignRequest> {

    @Override
    public boolean isValid(CreateCampaignRequest request, ConstraintValidatorContext context) {
        if (request == null || request.type() == null) {
            return true;
        }

        boolean valid = true;

        context.disableDefaultConstraintViolation();

        switch (request.type()) {
            case DONATION -> {
                if (request.targetBudget() == null || request.targetBudget() <= 0) {
                    context.buildConstraintViolationWithTemplate("Le budget cible est obligatoire pour une campagne de don et doit être supérieur à 0.")
                            .addPropertyNode("targetBudget").addConstraintViolation();
                    valid = false;
                }
                if (request.targetVolunteer() == null || request.targetVolunteer() <= 0) {
                    context.buildConstraintViolationWithTemplate("Le nombre de bénévoles cible est obligatoire pour une campagne de don et doit être supérieur à 0.")
                            .addPropertyNode("targetVolunteer").addConstraintViolation();
                    valid = false;
                }
            }

            case VOLUNTEERING -> {
                if (request.targetVolunteer() == null || request.targetVolunteer() <= 0) {
                    context.buildConstraintViolationWithTemplate("Le nombre de volontaires est obligatoire pour une campagne de volontariat et doit être supérieur à 0.")
                            .addPropertyNode("targetVolunteer").addConstraintViolation();
                    valid = false;
                }
            }

            case INVESTMENT -> {
                if (request.shareOffered() == null || request.shareOffered() <= 0 || request.shareOffered() > 100) {
                    context.buildConstraintViolationWithTemplate("La part offerte est obligatoire pour une campagne d’investissement et doit être comprise entre 0 et 100.")
                            .addPropertyNode("shareOffered").addConstraintViolation();
                    valid = false;
                }
            }
        }

        return valid;
    }
}

