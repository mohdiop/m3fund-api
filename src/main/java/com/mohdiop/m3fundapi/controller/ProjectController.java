package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.ProjectService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthenticationService authenticationService;
    private final CampaignService campaignService;

    public ProjectController(ProjectService projectService, AuthenticationService authenticationService, CampaignService campaignService) {
        this.projectService = projectService;
        this.authenticationService = authenticationService;
        this.campaignService = campaignService;
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @ModelAttribute CreateProjectRequest createProjectRequest
    ) {
        return new ResponseEntity<>(
                projectService.createProject(
                        authenticationService.getCurrentUserId(),
                        createProjectRequest
                ),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @PostMapping("/{projectId}/validate")
    public ResponseEntity<OwnerProjectResponse> validateProject(
            @PathVariable Long projectId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                projectService.validateProject(projectId)
        );
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @PostMapping("/campaigns/batch")
    public ResponseEntity<List<ProjectResponse>> getProjectsByAllCampaigns(
            @RequestBody List<Long> campaignsId
            ) {
        return ResponseEntity.ok(
                campaignService.getProjectsByAllCampaigns(campaignsId)
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest updateProjectRequest
            ) throws AccessDeniedException {
        return ResponseEntity.ok(
                projectService.updateProject(
                        id,
                        updateProjectRequest,
                        authenticationService.getCurrentUserId()
                )
        );
    }
}
