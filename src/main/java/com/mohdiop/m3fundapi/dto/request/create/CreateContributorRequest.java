package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.annotation.FileContentTypeIfPresent;
import com.mohdiop.m3fundapi.annotation.FileNotEmptyIfPresent;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record CreateContributorRequest(

        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(min = 2, max = 50, message = "Le prénom doit comporter entre 2 et 50 caractères.")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(min = 2, max = 50, message = "Le nom doit comporter entre 2 et 50 caractères.")
        String lastName,

        @Valid
        CreateLocalizationRequest localization,

        @NotNull(message = "Les préférences de domaines de projet de l'utilisateur sont obligatoires.")
        Set<ProjectDomain> projectDomainPrefs,

        @NotNull(message = "Les préférences de type de campagne de l'utilisateur sont obligatoires.")
        Set<CampaignType> campaignTypePrefs,

        @Email(message = "Le format de l'adresse e-mail est invalide.")
        @Size(max = 100, message = "L'adresse e-mail ne doit pas dépasser 100 caractères.")
        String email,

        @NotBlank(message = "Le numéro de téléphone est obligatoire.")
        @Pattern(
                regexp = "^\\+[1-9]\\d{1,3}[- ]?\\d{6,14}$",
                message = "Le numéro de téléphone doit inclure l'indicatif international (ex: +223 71234567)."
        )
        String phone,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, max = 64, message = "Le mot de passe doit contenir entre 8 et 64 caractères.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial."
        )
        String password,

        @FileNotEmptyIfPresent(message = "Le fichier de la photo de profil ne peut pas être vide.")
        @FileContentTypeIfPresent(allowed = {
                "image/jpg",
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/webp",
                "image/bmp",
                "image/tiff",
                "image/svg+xml",
                "image/heic",
                "image/heif",
                "image/avif"
        }
                , message = "La photo de profil doit être au format JPG ou PNG.")
        MultipartFile profilePicture
) {

    public Contributor toContributor() {
        return Contributor
                .builder()
                .id(null)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .projectDomains(projectDomainPrefs)
                .campaignTypes(campaignTypePrefs)
                .state(UserState.ACTIVE)
                .userRoles(new HashSet<>(List.of(UserRole.ROLE_CONTRIBUTOR)))
                .userCreatedAt(LocalDateTime.now())
                .localization(
                        localization == null ? null : localization.toLocalization()
                )
                .build();
    }
}
