package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.entity.File;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UploadService uploadService;

    public ProjectService(ProjectRepository projectRepository, UploadService uploadService) {
        this.projectRepository = projectRepository;
        this.uploadService = uploadService;
    }

    public ProjectResponse createProject(
            CreateProjectRequest createProjectRequest
    ) {
        Project project = createProjectRequest.toProject();
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
}
