package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectsStatsResponse;
import com.mohdiop.m3fundapi.entity.*;
import com.mohdiop.m3fundapi.entity.enums.*;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import com.mohdiop.m3fundapi.repository.ValidationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UploadService uploadService;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final ValidationRequestRepository validationRequestRepository;

    public ProjectService(ProjectRepository projectRepository, UploadService uploadService, ProjectOwnerRepository projectOwnerRepository, ValidationRequestRepository validationRequestRepository) {
        this.projectRepository = projectRepository;
        this.uploadService = uploadService;
        this.projectOwnerRepository = projectOwnerRepository;
        this.validationRequestRepository = validationRequestRepository;
    }

    @Transactional
    public ProjectResponse createProject(
            Long ownerId,
            CreateProjectRequest createProjectRequest
    ) throws BadRequestException {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Porteur introuvable!")
                );
        if(owner.getState() == UserState.PENDING_VALIDATION) {
            throw new BadRequestException("Impossible de créer un projet car vos informations sont en cours de validation suite à une récente modification de votre profil.");
        }
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
        var projectResponse = projectRepository.save(project);
        validationRequestRepository.save(
                ValidationRequest.builder()
                        .id(null)
                        .project(projectResponse)
                        .state(ValidationState.PENDING)
                        .date(LocalDateTime.now())
                        .entity(EntityName.PROJECT)
                        .type(ValidationType.CREATION)
                        .build()
        );
        return projectResponse.toResponse();
    }

    public List<OwnerProjectResponse> getAllProjects() {
        var allProjects = projectRepository.findAll();
        return allProjects.stream().map(Project::toOwnerProjectResponse).toList();
    }

    @Transactional
    public ProjectResponse updateProject(
            Long projectId,
            UpdateProjectRequest request,
            Long demanderId
    ) throws AccessDeniedException {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Projet introuvable."));

        var owner = projectOwnerRepository.findById(demanderId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        if (!Objects.equals(project.getOwner().getId(), owner.getId())) {
            throw new AccessDeniedException("Accès refusé.");
        }

        var changed = false;

        if (request.name() != null && !request.name().equals(project.getName())) {
            project.setName(request.name());
            changed = true;
        }
        if (request.resume() != null && !request.resume().equals(project.getResume())) {
            project.setResume(request.resume());
            changed = true;
        }
        if (request.description() != null && !request.description().equals(project.getDescription())) {
            project.setDescription(request.description());
            changed = true;
        }
        if (request.domain() != null && request.domain() != project.getDomain()) {
            project.setDomain(request.domain());
            changed = true;
        }
        if (request.objective() != null && !request.objective().equals(project.getObjective())) {
            project.setObjective(request.objective());
            changed = true;
        }
        if (request.websiteLink() != null && !request.websiteLink().equals(project.getWebsiteLink())) {
            project.setWebsiteLink(request.websiteLink());
            changed = true;
        }
        if (request.launchedAt() != null) {
            project.setLaunchedAt(request.launchedAt());
            changed = true;
        }

        if (request.images() != null && !request.images().isEmpty()) {
            project.getImages().clear();
            project = projectRepository.save(project);
            project.setImages(
                    request.images().stream().map(
                            (i) -> uploadService.uploadFile(
                                    i,
                                    UUID.randomUUID().toString(),
                                    FileType.PICTURE,
                                    uploadService.getFileExtension(i)
                            )
                    ).collect(Collectors.toSet())
            );
            changed = true;
        }

        if (request.video() != null && !request.video().isEmpty()) {
            project.setVideo(
                    uploadService.uploadFile(
                            request.video(),
                            project.getVideo().getName(),
                            project.getVideo().getType(),
                            uploadService.getFileExtension(request.video())
                    )
            );
            changed = true;
        }

        if (request.businessPlan() != null && !request.businessPlan().isEmpty()) {
            project.setBusinessPlan(
                    uploadService.uploadFile(
                            request.businessPlan(),
                            project.getBusinessPlan().getName(),
                            project.getBusinessPlan().getType(),
                            uploadService.getFileExtension(request.businessPlan())
                    )
            );
            changed = true;
        }

        if(changed) {
            project.setValidated(false);
            for (Campaign campaign : project.getCampaigns()) {
                campaign.setState(CampaignState.SUSPENDED);
            }
            validationRequestRepository.save(
                    ValidationRequest.builder()
                            .id(null)
                            .project(project)
                            .state(ValidationState.PENDING)
                            .type(ValidationType.MODIFICATION)
                            .entity(EntityName.PROJECT)
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        var saved = projectRepository.save(project);
        return saved.toResponse();
    }

    public List<ProjectResponse> getMyProjects(
            Long ownerId
    ) {
        var projects = projectRepository.findByOwnerId(ownerId);
        if (projects.isEmpty()) return new ArrayList<>();
        return projects.stream().map(Project::toResponse).toList();
    }

    public List<ProjectResponse> getMyValidatedProjects(
            Long ownerId
    ) {
        var projects = projectRepository.findByOwnerIdAndIsValidated(ownerId, true);
        if (projects.isEmpty()) return new ArrayList<>();
        return projects.stream().map(Project::toResponse).toList();
    }

    public List<ProjectResponse> getMyUnvalidatedProjects(
            Long ownerId
    ) {
        var projects = projectRepository.findByOwnerIdAndIsValidated(ownerId, false);
        if (projects.isEmpty()) return new ArrayList<>();
        return projects.stream().map(Project::toResponse).toList();
    }

    public ProjectsStatsResponse getMyProjectsStats(
            Long ownerId
    ) {
        var projects = projectRepository.findByOwnerId(ownerId);
        if (projects.isEmpty()) return new ProjectsStatsResponse(0L, 0L, 0L, 0L);

        long totalProjects = projects.size();
        long validatedProjects = projects.stream()
                .filter(Project::isValidated)
                .count();
        long pendingProjects = projects.stream()
                .filter(p -> !p.isValidated())
                .count();
        long projectsWithActiveCampaigns = projects.stream()
                .filter(p -> p.getCampaigns() != null &&
                        p.getCampaigns().stream().anyMatch(
                                campaign -> campaign.getState() == CampaignState.IN_PROGRESS
                        ))
                .count();
        return new ProjectsStatsResponse(
                totalProjects,
                validatedProjects,
                pendingProjects,
                projectsWithActiveCampaigns
        );
    }

    public List<ProjectResponse> searchProjectsByTerm(
            Long ownerId,
            String searchTerm
    ) {
        var projects = projectRepository.searchOwnerProjects(ownerId, searchTerm);
        if (projects.isEmpty()) return new ArrayList<>();
        return projects.stream().map(Project::toResponse).toList();
    }

    public List<ProjectResponse> getProjectsByDomain(
            Long ownerId,
            ProjectDomain domain
    ) {
        var projects = projectRepository.findByOwnerIdAndDomain(ownerId, domain);
        if (projects.isEmpty()) return new ArrayList<>();
        return projects.stream().map(Project::toResponse).toList();
    }
}
