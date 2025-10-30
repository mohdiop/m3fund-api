package com.mohdiop.m3fundapi.dto.request.update;

import com.mohdiop.m3fundapi.annotation.FileContentTypeIfPresent;
import com.mohdiop.m3fundapi.annotation.FileNotEmptyIfPresent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileRequest(
        @Size(min = 2, max = 50, message = "Le prénom doit comporter entre 2 et 50 caractères.")
        String firstName,

        @Size(min = 2, max = 50, message = "Le nom doit comporter entre 2 et 50 caractères.")
        String lastName,

        @Email(message = "Le format de l'adresse e-mail est invalide.")
        @Size(max = 100, message = "L'adresse e-mail ne doit pas dépasser 100 caractères.")
        String email,

        @Pattern(
                regexp = "^\\+[1-9]\\d{1,3}[- ]?\\d{6,14}$",
                message = "Le numéro de téléphone doit inclure l'indicatif international (ex: +223 71234567)."
        )
        String phone,

        @Size(min = 5, max = 255, message = "L'adresse doit comporter entre 5 et 255 caractères.")
        String address,

        @NotBlank(message = "Le mot de passe actuel est obligatoire pour confirmer les modifications.")
        @Size(min = 8, max = 64, message = "Le mot de passe doit contenir entre 8 et 64 caractères.")
        String currentPassword,

        @FileNotEmptyIfPresent(message = "Le fichier de la photo de profil ne peut pas être vide.")
        @FileContentTypeIfPresent(allowed = {"image/jpeg", "image/png"}, message = "La photo de profil doit être au format JPG ou PNG.")
        MultipartFile profilePhoto
) {
}
