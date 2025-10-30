package com.mohdiop.m3fundapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Le mot de passe actuel est obligatoire.")
        String currentPassword,

        @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
        @Size(min = 8, max = 64, message = "Le nouveau mot de passe doit contenir entre 8 et 64 caractères.")
        String newPassword,

        @NotBlank(message = "La confirmation du nouveau mot de passe est obligatoire.")
        String confirmPassword
) {
}
