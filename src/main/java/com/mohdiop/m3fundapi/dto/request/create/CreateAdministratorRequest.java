package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Administrator;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;

public record CreateAdministratorRequest(
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
                message = "Le numéro de téléphone doit inclure l'indicatif international (ex: +33 612345678)."
        )
        String phone,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, max = 64, message = "Le mot de passe doit contenir entre 8 et 64 caractères.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial."
        )
        String password,

        @NotNull(message = "Les rôles utilisateur sont obligatoires.")
        @Size(min = 1, message = "L'utilisateur doit avoir au moins un rôle.")
        Set<UserRole> userRoles
) {
    public Administrator toAdministrator() {
        return Administrator.builder()
                .id(null)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .userRoles(userRoles)
                .userCreatedAt(LocalDateTime.now())
                .state(UserState.ACTIVE)
                .build();
    }
}
