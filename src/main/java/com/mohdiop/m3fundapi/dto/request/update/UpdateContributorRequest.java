package com.mohdiop.m3fundapi.dto.request.update;

import com.mohdiop.m3fundapi.dto.request.create.CreateLocalizationRequest;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateContributorRequest(

        @Size(min = 2, max = 50,
                message = "Le prénom doit comporter entre 2 et 50 caractères.")
        String firstName,

        @Size(min = 2, max = 50,
                message = "Le nom doit comporter entre 2 et 50 caractères.")
        String lastName,

        Set<ProjectDomain> projectDomainPrefs,

        Set<CampaignType> campaignTypePrefs,

        CreateLocalizationRequest localization,

        @Email(message = "Le format de l'adresse e-mail est invalide.")
        @Size(max = 100,
                message = "L'adresse e-mail ne doit pas dépasser 100 caractères.")
        String email,

        @Pattern(
                regexp = "^\\+[1-9]\\d{1,3}[- ]?\\d{6,14}$",
                message = "Le numéro de téléphone doit inclure l'indicatif international (ex: +223 71234567)."
        )
        String phone,

        @Size(min = 8, max = 64,
                message = "Le mot de passe doit contenir entre 8 et 64 caractères.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial."
        )
        String password
) {
}
