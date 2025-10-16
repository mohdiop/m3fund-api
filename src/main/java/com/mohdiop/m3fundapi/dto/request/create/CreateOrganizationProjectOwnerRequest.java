package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.annotation.FileContentType;
import com.mohdiop.m3fundapi.annotation.FileContentTypeIfPresent;
import com.mohdiop.m3fundapi.annotation.FileNotEmpty;
import com.mohdiop.m3fundapi.annotation.FileNotEmptyIfPresent;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public record CreateOrganizationProjectOwnerRequest(
        @NotBlank(message = "Le nom de l'entité est obligatoire.")
        @Size(min = 2, max = 50, message = "Le nom de l'entité doit comporter entre 2 et 50 caractères.")
        String entityName,

        @NotBlank(message = "L'adresse e-mail est obligatoire.")
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

        @NotBlank(message = "L'adresse est obligatoire.")
        @Size(min = 5, max = 255, message = "L'adresse doit comporter entre 5 et 255 caractères.")
        String address,

        @NotNull(message = "Le revenu annuel est obligatoire.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le revenu annuel doit être supérieur à 0.")
        @Digits(integer = 12, fraction = 2, message = "Le revenu annuel doit être un nombre valide avec au maximum 2 décimales.")
        Double annualIncome,

        @NotNull(message = "Le capital social est obligatoire.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le capital social doit être supérieur à 0.")
        @Digits(integer = 12, fraction = 2, message = "Le capital social doit être un nombre valide avec au maximum 2 décimales.")
        Double shareCapital,

        @FileNotEmptyIfPresent(message = "Le fichier du logo ne peut pas être vide.")
        @FileContentTypeIfPresent(
                allowed = {"image/jpeg", "image/png"},
                message = "Le logo doit être au format JPG ou PNG."
        )
        MultipartFile logo,

        @NotNull(message = "Le rccm de l'entreprise est obligatoire.")
        @FileNotEmpty(message = "Le fichier du rccm de l'entreprise ne peut pas être vide.")
        @FileContentType(
                allowed = {"image/jpeg", "image/png", "application/pdf"},
                message = "Le rccm de l'entreprise doit être une image ou un PDF."
        )
        MultipartFile rccm,

        @NotNull(message = "Le relevé bancaire est obligatoire.")
        @FileNotEmpty(message = "Le fichier du relevé bancaire ne peut pas être vide.")
        @FileContentType(
                allowed = {"application/pdf"},
                message = "Le relevé bancaire doit être au format PDF."
        )
        MultipartFile bankStatement
) {

    public ProjectOwner toOrganizationProjectOwner() {
        return ProjectOwner.builder()
                .id(null)
                .entityName(entityName)
                .email(email)
                .phone(phone)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .address(address)
                .annualIncome(annualIncome)
                .shareCapital(shareCapital)
                .state(UserState.INACTIVE)
                .userCreatedAt(LocalDateTime.now())
                .userRoles(new HashSet<>(List.of(UserRole.ROLE_CONTRIBUTOR)))
                .type(ProjectOwnerType.ORGANIZATION)
                .build();
    }
}
