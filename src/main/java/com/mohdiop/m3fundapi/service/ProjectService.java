package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.entity.File;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

        if (request.name() != null) project.setName(request.name());
        if (request.resume() != null) project.setResume(request.resume());
        if (request.description() != null) project.setDescription(request.description());
        if (request.domain() != null) project.setDomain(request.domain());
        if (request.objective() != null) project.setObjective(request.objective());
        if (request.websiteLink() != null) project.setWebsiteLink(request.websiteLink());
        if (request.launchedAt() != null) project.setLaunchedAt(request.launchedAt());

        if (request.images() != null && !request.images().isEmpty()) {
            project.getImages().clear();
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
        }

        var saved = projectRepository.save(project);
        return saved.toResponse();
    }

}
