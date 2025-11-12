package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.annotation.FileContentType;
import com.mohdiop.m3fundapi.annotation.FileNotEmpty;
import com.mohdiop.m3fundapi.annotation.ValidFileList;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

public record CreateProjectRequest(

        @NotBlank(message = "Le nom du projet est obligatoire.")
        @Size(min = 3, max = 100, message = "Le nom du projet doit comporter entre 3 et 100 caractères.")
        String name,

        @NotBlank(message = "Le résumé est obligatoire.")
        @Size(min = 10, max = 500, message = "Le résumé doit comporter entre 10 et 500 caractères.")
        String resume,

        @NotBlank(message = "La description est obligatoire.")
        @Size(min = 20, message = "La description doit contenir au moins 20 caractères.")
        String description,

        @NotNull(message = "Le domaine du projet est obligatoire.")
        ProjectDomain domain,

        @NotBlank(message = "L'objectif est obligatoire.")
        @Size(min = 10, max = 300, message = "L'objectif doit comporter entre 10 et 300 caractères.")
        String objective,

        @NotBlank(message = "Le lien du site web est obligatoire.")
        @Pattern(
                regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "Le lien du site web doit être une URL valide."
        )
        String websiteLink,

        @NotNull(message = "La date de lancement est obligatoire.")
        @PastOrPresent(message = "La date de lancement doit être antérieure ou égale à la date actuelle.")
        LocalDateTime launchedAt,

        @NotEmpty(message = "Au moins une image est obligatoire.")
        @Size(min = 1, max = 6, message = "Vous devez fournir entre 1 et 6 images.")
        @ValidFileList(
                allowed = {"image/jpeg", "image/png", "image/avif", "image/webp"},
                message = "Toutes les images doivent être au format JPG, PNG, AVIF ou WEBP."
        )
        Set<MultipartFile> images,

        @NotNull(message = "La vidéo du projet est obligatoire.")
        @FileNotEmpty(message = "Le fichier vidéo ne peut pas être vide.")
        @FileContentType(
                allowed = {"video/mp4", "video/mpeg", "video/quicktime"},
                message = "La vidéo doit être au format MP4, MPEG ou MOV."
        )
        MultipartFile video,

        @NotNull(message = "Le business plan est obligatoire.")
        @FileNotEmpty(message = "Le fichier du business plan ne peut pas être vide.")
        @FileContentType(
                allowed = {"application/pdf"},
                message = "Le business plan doit être au format PDF."
        )
        MultipartFile businessPlan

) {

    public Project toProject() {
        return Project.builder()
                .id(null)
                .name(name)
                .resume(resume)
                .description(description)
                .domain(domain)
                .objective(objective)
                .websiteLink(websiteLink)
                .isValidated(false)
                .launchedAt(launchedAt)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
