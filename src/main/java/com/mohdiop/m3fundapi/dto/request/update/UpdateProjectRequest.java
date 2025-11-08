package com.mohdiop.m3fundapi.dto.request.update;

import com.mohdiop.m3fundapi.annotation.FileContentType;
import com.mohdiop.m3fundapi.annotation.FileNotEmpty;
import com.mohdiop.m3fundapi.annotation.ValidFileList;
import com.mohdiop.m3fundapi.annotation.ValidFileListForPatch;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateProjectRequest(

        @Size(min = 3, max = 100, message = "Le nom du projet doit comporter entre 3 et 100 caractères.")
        String name,

        @Size(min = 10, max = 500, message = "Le résumé doit comporter entre 10 et 500 caractères.")
        String resume,

        @Size(min = 20, message = "La description doit contenir au moins 20 caractères.")
        String description,

        ProjectDomain domain,

        @Size(min = 10, max = 300, message = "L'objectif doit comporter entre 10 et 300 caractères.")
        String objective,

        @Pattern(
                regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$",
                message = "Le lien du site web doit être une URL valide."
        )
        String websiteLink,

        @PastOrPresent(message = "La date de lancement doit être antérieure ou égale à la date actuelle.")
        LocalDateTime launchedAt,

        @Size(max = 6, message = "Vous pouvez fournir au maximum 6 images.")
        @ValidFileListForPatch(
                allowed = {"image/jpeg", "image/png"},
                message = "Toutes les images doivent être au format JPG ou PNG."
        )
        Set<MultipartFile> images,

        @FileContentType(
                allowed = {"video/mp4", "video/mpeg", "video/quicktime"},
                message = "La vidéo doit être au format MP4, MPEG ou MOV."
        )
        MultipartFile video,

        @FileContentType(
                allowed = {"application/pdf"},
                message = "Le business plan doit être au format PDF."
        )
        MultipartFile businessPlan

) {
}
