package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectStatsResponse;
import com.mohdiop.m3fundapi.entity.File;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UploadService uploadService;
    private final ProjectOwnerRepository projectOwnerRepository;

    public ProjectService(ProjectRepository projectRepository, UploadService uploadService, ProjectOwnerRepository projectOwnerRepository) {
        this.projectRepository = projectRepository;
        this.uploadService = uploadService;
        this.projectOwnerRepository = projectOwnerRepository;
    }

    public ProjectResponse createProject(
            Long ownerId,
            CreateProjectRequest createProjectRequest
    ) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Porteur introuvable!")
                );
        Project project = createProjectRequest.toProject();
        project.setOwner(owner);
        if (!createProjectRequest.images().isEmpty()) {
            Set<File> allImages = new HashSet<>(Set.of());
            for (var image : createProjectRequest.images()) {
                if (image != null) {
                    allImages.add(
                            uploadService.uploadFile(
                                    image,
                                    UUID.randomUUID().toString(),
                                    FileType.PICTURE,
                                    uploadService.getFileExtension(image)
                            )
                    );
                }
            }
            project.setImages(allImages);
        }
        if (createProjectRequest.businessPlan() != null) {
            project.setBusinessPlan(
                    uploadService.uploadFile(
                            createProjectRequest.businessPlan(),
                            UUID.randomUUID().toString(),
                            FileType.BUSINESS_MODEL,
                            uploadService.getFileExtension(createProjectRequest.businessPlan())
                    )
            );
        }
        if (createProjectRequest.video() != null) {
            project.setVideo(
                    uploadService.uploadFile(
                            createProjectRequest.video(),
                            UUID.randomUUID().toString(),
                            FileType.VIDEO,
                            uploadService.getFileExtension(createProjectRequest.video())
                    )
            );
        }
        return projectRepository.save(project).toResponse();
    }

    public OwnerProjectResponse validateProject(
            Long projectId
    ) throws BadRequestException {
        Project projectToValidate = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable!")
                );
        if (projectToValidate.isValidated()) {
            throw new BadRequestException("Projet déjà validé.");
        }
        projectToValidate.setValidated(true);
        return projectRepository.save(projectToValidate).toOwnerProjectResponse();
    }

    public List<OwnerProjectResponse> getAllProjects() {
        var allProjects = projectRepository.findAll();
        return allProjects.stream().map(Project::toOwnerProjectResponse).toList();
    }

    public List<OwnerProjectResponse> searchProjects(String searchTerm) {
        var projects = projectRepository.searchProjects(searchTerm);
        return projects.stream().map(Project::toOwnerProjectResponse).toList();
    }

    public List<OwnerProjectResponse> filterByValidationStatus(boolean isValidated) {
        var projects = projectRepository.findByIsValidated(isValidated);
        return projects.stream().map(Project::toOwnerProjectResponse).toList();
    }

    public List<OwnerProjectResponse> filterByDomain(String domainStr) {
        try {
            ProjectDomain domain = ProjectDomain.valueOf(domainStr.toUpperCase());
            var projects = projectRepository.findByDomain(domain);
            return projects.stream().map(Project::toOwnerProjectResponse).toList();
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Domaine invalide: " + domainStr);
        }
    }

    public List<OwnerProjectResponse> getValidatedProjects() {
        return filterByValidationStatus(true);
    }

    public List<OwnerProjectResponse> getPendingProjects() {
        return filterByValidationStatus(false);
    }

    public ProjectStatsResponse getProjectStats() {
        var allProjects = projectRepository.findAll();
        
        long totalProjects = allProjects.size();
        long validatedProjects = allProjects.stream()
                .filter(Project::isValidated)
                .count();
        long pendingProjects = allProjects.stream()
                .filter(project -> !project.isValidated())
                .count();
        
        // Compter les projets avec des campagnes actives
        long projectsWithActiveCampaigns = allProjects.stream()
                .filter(project -> project.getCampaigns() != null && 
                        project.getCampaigns().stream()
                                .anyMatch(campaign -> campaign.getState() == CampaignState.IN_PROGRESS))
                .count();
        
        return new ProjectStatsResponse(
                totalProjects,
                validatedProjects,
                pendingProjects,
                projectsWithActiveCampaigns
        );
    }

    public OwnerProjectResponse updateProject(
            Long projectId,
            Long ownerId,
            UpdateProjectRequest updateProjectRequest
    ) throws AccessDeniedException, BadRequestException {
        // Charger le projet avec ses images pour éviter les problèmes d'orphanRemoval
        Project project = projectRepository.findByIdWithImages(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable!")
                );

        // Initialiser la collection d'images si elle est null (peut arriver si aucune image n'existe)
        if (project.getImages() == null) {
            project.setImages(new HashSet<>());
        }

        // Vérifier que l'utilisateur est bien le propriétaire du projet
        if (!Objects.equals(project.getOwner().getId(), ownerId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier ce projet.");
        }

        // Mettre à jour les champs textuels si fournis
        if (updateProjectRequest.name() != null && !updateProjectRequest.name().isBlank()) {
            project.setName(updateProjectRequest.name());
        }
        if (updateProjectRequest.resume() != null && !updateProjectRequest.resume().isBlank()) {
            project.setResume(updateProjectRequest.resume());
        }
        if (updateProjectRequest.description() != null && !updateProjectRequest.description().isBlank()) {
            project.setDescription(updateProjectRequest.description());
        }
        if (updateProjectRequest.domain() != null) {
            project.setDomain(updateProjectRequest.domain());
        }
        if (updateProjectRequest.objective() != null && !updateProjectRequest.objective().isBlank()) {
            project.setObjective(updateProjectRequest.objective());
        }
        if (updateProjectRequest.websiteLink() != null && !updateProjectRequest.websiteLink().isBlank()) {
            project.setWebsiteLink(updateProjectRequest.websiteLink());
        }
        if (updateProjectRequest.launchedAt() != null) {
            project.setLaunchedAt(updateProjectRequest.launchedAt());
        }

        // Mettre à jour les images si fournies - AJOUTER aux images existantes au lieu de remplacer
        if (updateProjectRequest.images() != null && !updateProjectRequest.images().isEmpty()) {
            // Vérifier le nombre total d'images (existantes + nouvelles)
            int currentImageCount = project.getImages() != null ? project.getImages().size() : 0;
            int newImageCount = (int) updateProjectRequest.images().stream()
                    .filter(img -> img != null && !img.isEmpty())
                    .count();
            int totalImageCount = currentImageCount + newImageCount;

            // Vérifier que le total ne dépasse pas 6 images
            if (totalImageCount > 6) {
                throw new BadRequestException("Le projet ne peut pas avoir plus de 6 images au total. " +
                        "Actuellement: " + currentImageCount + ", nouvelles: " + newImageCount);
            }

            // Ajouter les nouvelles images à la collection existante
            Set<File> newImages = new HashSet<>();
            for (var image : updateProjectRequest.images()) {
                if (image != null && !image.isEmpty()) {
                    newImages.add(
                            uploadService.uploadFile(
                                    image,
                                    UUID.randomUUID().toString(),
                                    FileType.PICTURE,
                                    uploadService.getFileExtension(image)
                            )
                    );
                }
            }
            // Ajouter les nouvelles images à la collection existante au lieu de la remplacer
            project.getImages().addAll(newImages);
        }

        // Mettre à jour la vidéo si fournie
        if (updateProjectRequest.video() != null && !updateProjectRequest.video().isEmpty()) {
            project.setVideo(
                    uploadService.uploadFile(
                            updateProjectRequest.video(),
                            UUID.randomUUID().toString(),
                            FileType.VIDEO,
                            uploadService.getFileExtension(updateProjectRequest.video())
                    )
            );
        }

        // Mettre à jour le business plan si fourni
        if (updateProjectRequest.businessPlan() != null && !updateProjectRequest.businessPlan().isEmpty()) {
            project.setBusinessPlan(
                    uploadService.uploadFile(
                            updateProjectRequest.businessPlan(),
                            UUID.randomUUID().toString(),
                            FileType.BUSINESS_MODEL,
                            uploadService.getFileExtension(updateProjectRequest.businessPlan())
                    )
            );
        }

        return projectRepository.save(project).toOwnerProjectResponse();
    }

    public List<OwnerProjectResponse> getProjectsByOwner(Long ownerId) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Porteur introuvable."));
        
        var projects = projectRepository.findByOwnerId(ownerId);
        return projects.stream()
                .map(Project::toOwnerProjectResponse)
                .toList();
    }

    public List<OwnerProjectResponse> getValidatedProjectsByOwner(Long ownerId) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Porteur introuvable."));
        
        var projects = projectRepository.findByOwnerIdAndIsValidated(ownerId, true);
        return projects.stream()
                .map(Project::toOwnerProjectResponse)
                .toList();
    }
    
    /**
     * Récupère les statistiques des projets d'un propriétaire
     */
    public ProjectStatsResponse getProjectStatsByOwner(Long ownerId) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Porteur introuvable."));
        
        var projects = projectRepository.findByOwnerId(ownerId);
        
        long totalProjects = projects.size();
        long validatedProjects = projects.stream()
                .filter(Project::isValidated)
                .count();
        long pendingProjects = projects.stream()
                .filter(project -> !project.isValidated())
                .count();
        
        // Compter les projets avec des campagnes actives
        long projectsWithActiveCampaigns = projects.stream()
                .filter(project -> project.getCampaigns() != null && 
                        project.getCampaigns().stream()
                                .anyMatch(campaign -> campaign.getState() == CampaignState.IN_PROGRESS))
                .count();
        
        return new ProjectStatsResponse(
                totalProjects,
                validatedProjects,
                pendingProjects,
                projectsWithActiveCampaigns
        );
    }
    
    /**
     * Recherche des projets d'un propriétaire
     */
    public List<OwnerProjectResponse> searchProjectsByOwner(Long ownerId, String searchTerm) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Porteur introuvable."));
        
        var projects = projectRepository.searchProjectsByOwner(ownerId, searchTerm);
        return projects.stream()
                .map(Project::toOwnerProjectResponse)
                .toList();
    }
    
    /**
     * Filtre les projets d'un propriétaire par statut de validation
     */
    public List<OwnerProjectResponse> filterProjectsByOwnerAndStatus(Long ownerId, String status) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Porteur introuvable."));
        
        boolean isValidated;
        if ("validated".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status)) {
            isValidated = true;
        } else if ("pending".equalsIgnoreCase(status)) {
            isValidated = false;
        } else {
            // Si le statut n'est pas reconnu, retourner une liste vide
            return List.of();
        }
        
        var projects = projectRepository.findByOwnerIdAndIsValidated(ownerId, isValidated);
        return projects.stream()
                .map(Project::toOwnerProjectResponse)
                .toList();
    }
    
    /**
     * Filtre les projets d'un propriétaire par domaine
     */
    public List<OwnerProjectResponse> filterProjectsByOwnerAndDomain(Long ownerId, String domainStr) {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Porteur introuvable."));
        
        try {
            ProjectDomain domain = ProjectDomain.valueOf(domainStr.toUpperCase());
            var projects = projectRepository.findByOwnerIdAndDomain(ownerId, domain);
            return projects.stream()
                    .map(Project::toOwnerProjectResponse)
                    .toList();
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
}
