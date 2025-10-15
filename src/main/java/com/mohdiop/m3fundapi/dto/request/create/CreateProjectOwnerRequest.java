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

public record CreateProjectOwnerRequest(
        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(min = 2, max = 50, message = "Le prénom doit comporter entre 2 et 50 caractères.")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(min = 2, max = 50, message = "Le nom doit comporter entre 2 et 50 caractères.")
        String lastName,

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

        @FileNotEmptyIfPresent(message = "Le fichier de la photo de profil ne peut pas être vide.")
        @FileContentTypeIfPresent(allowed = {"image/jpeg", "image/png"}, message = "La photo de profil doit être au format JPG ou PNG.")
        MultipartFile profilePicture,

        @NotNull(message = "La carte biométrique est obligatoire.")
        @FileNotEmpty(message = "Le fichier de la carte biométrique ne peut pas être vide.")
        @FileContentType(allowed = {"image/jpeg", "image/png", "application/pdf"}, message = "La carte biométrique doit être une image ou un PDF.")
        MultipartFile biometricCard,

        @NotNull(message = "Le certificat de résidence est obligatoire.")
        @FileNotEmpty(message = "Le fichier du certificat de résidence ne peut pas être vide.")
        @FileContentType(allowed = {"application/pdf", "image/jpeg", "image/png"}, message = "Le certificat de résidence doit être une image ou un PDF.")
        MultipartFile residenceCertificate,

        @FileNotEmptyIfPresent(message = "Le fichier du relevé bancaire ne peut pas être vide.")
        @FileContentTypeIfPresent(allowed = {"application/pdf"}, message = "Le relevé bancaire doit être au format PDF.")
        MultipartFile bankStatement
) {

    public ProjectOwner toIndividualProjectOwner() {
        return ProjectOwner.builder()
                .id(null)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .address(address)
                .annualIncome(annualIncome)
                .userRoles(new HashSet<>(List.of(UserRole.ROLE_PROJECT_OWNER)))
                .type(ProjectOwnerType.INDIVIDUAL)
                .state(UserState.ACTIVE)
                .userCreatedAt(LocalDateTime.now())
                .build();
    }
}
