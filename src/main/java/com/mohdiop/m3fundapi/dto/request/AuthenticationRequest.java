package com.mohdiop.m3fundapi.dto.request;

import com.mohdiop.m3fundapi.entity.enums.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @NotBlank(message = "Email invalide.") String username,
        @NotBlank(message = "Mot de passe invalide.") String password,
        @NotNull(message = "La plateforme est obligatoire.") PlatformType platform
) {
}