package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.update.UpdateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ProjectOwnerService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project-owners")
public class ProjectOwnerController {

    private final ProjectOwnerService projectOwnerService;
    private final AuthenticationService authenticationService;

    public ProjectOwnerController(ProjectOwnerService projectOwnerService, AuthenticationService authenticationService) {
        this.projectOwnerService = projectOwnerService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PatchMapping
    public ResponseEntity<IndividualProjectOwnerResponse> updateIndividualProjectOwner(
            UpdateIndividualProjectOwnerRequest updateIndividualProjectOwnerRequest
    ) throws BadRequestException {
        return ResponseEntity.ok(
                projectOwnerService.updateIndividualProjectOwner(
                        authenticationService.getCurrentUserId(),
                        updateIndividualProjectOwnerRequest
                )
        );
    }
}
