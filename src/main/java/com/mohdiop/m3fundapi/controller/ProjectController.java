package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProjectRequest;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectStatsResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
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

    public ProjectController(ProjectService projectService, AuthenticationService authenticationService) {
        this.projectService = projectService;
        this.authenticationService = authenticationService;
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

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PutMapping("/{projectId}")
    public ResponseEntity<OwnerProjectResponse> updateProject(
            @PathVariable Long projectId,
            @Valid @ModelAttribute UpdateProjectRequest updateProjectRequest
    ) throws AccessDeniedException {
        return ResponseEntity.ok(
                projectService.updateProject(
                        projectId,
                        authenticationService.getCurrentUserId(),
                        updateProjectRequest
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/my-projects")
    public ResponseEntity<List<OwnerProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(
                projectService.getProjectsByOwner(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/my-projects/validated")
    public ResponseEntity<List<OwnerProjectResponse>> getMyValidatedProjects() {
        return ResponseEntity.ok(
                projectService.getValidatedProjectsByOwner(
                        authenticationService.getCurrentUserId()
                )
        );
    }
    
    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/my-projects/stats")
    public ResponseEntity<ProjectStatsResponse> getMyProjectStats() {
        return ResponseEntity.ok(
                projectService.getProjectStatsByOwner(
                        authenticationService.getCurrentUserId()
                )
        );
    }
    
    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/my-projects/search")
    public ResponseEntity<List<OwnerProjectResponse>> searchMyProjects(
            @RequestParam("q") String searchTerm
    ) {
        return ResponseEntity.ok(
                projectService.searchProjectsByOwner(
                        authenticationService.getCurrentUserId(),
                        searchTerm
                )
        );
    }
    
    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/my-projects/status/{status}")
    public ResponseEntity<List<OwnerProjectResponse>> filterMyProjectsByStatus(
            @PathVariable String status
    ) {
        return ResponseEntity.ok(
                projectService.filterProjectsByOwnerAndStatus(
                        authenticationService.getCurrentUserId(),
                        status
                )
        );
    }
    
    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/my-projects/domain/{domain}")
    public ResponseEntity<List<OwnerProjectResponse>> filterMyProjectsByDomain(
            @PathVariable String domain
    ) {
        return ResponseEntity.ok(
                projectService.filterProjectsByOwnerAndDomain(
                        authenticationService.getCurrentUserId(),
                        domain
                )
        );
    }
}
