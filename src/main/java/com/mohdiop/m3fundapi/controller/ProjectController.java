package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectsStatsResponse;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.ProjectService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    ) throws BadRequestException {
        return new ResponseEntity<>(
                projectService.createProject(
                        authenticationService.getCurrentUserId(),
                        createProjectRequest
                ),
                HttpStatus.CREATED
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
            UpdateProjectRequest updateProjectRequest,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                projectService.updateProject(
                        id,
                        updateProjectRequest,
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/mine")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(
                projectService.getMyProjects(authenticationService.getCurrentUserId())
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/mine-validated")
    public ResponseEntity<List<ProjectResponse>> getMyValidatedProjects() {
        return ResponseEntity.ok(
                projectService.getMyValidatedProjects(authenticationService.getCurrentUserId())
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/mine-unvalidated")
    public ResponseEntity<List<ProjectResponse>> getMyUnvalidatedProjects() {
        return ResponseEntity.ok(
                projectService.getMyUnvalidatedProjects(authenticationService.getCurrentUserId())
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchInMyProjects(
            @RequestParam("q") String searchTerm
    ) {
        return ResponseEntity.ok(
                projectService.searchProjectsByTerm(
                        authenticationService.getCurrentUserId(),
                        searchTerm
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/domain")
    public ResponseEntity<List<ProjectResponse>> getProjectsByDomain(
            @RequestParam("d") ProjectDomain domain
    ) {
        return ResponseEntity.ok(
                projectService.getProjectsByDomain(
                        authenticationService.getCurrentUserId(),
                        domain
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/stats")
    public ResponseEntity<ProjectsStatsResponse> getMyProjectsStats() {
        return ResponseEntity.ok(
                projectService.getMyProjectsStats(authenticationService.getCurrentUserId())
        );
    }
}
