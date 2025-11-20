package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.update.UpdateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ProjectOwnerService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @Valid UpdateIndividualProjectOwnerRequest updateIndividualProjectOwnerRequest
    ) throws BadRequestException {
        return ResponseEntity.ok(
                projectOwnerService.updateIndividualProjectOwner(
                        authenticationService.getCurrentUserId(),
                        updateIndividualProjectOwnerRequest
                )
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USERS_ADMIN', 'VALIDATIONS_ADMIN', 'SYSTEM')")
    @GetMapping("/{ownerId}")
    public ResponseEntity<Record> getById(
            @PathVariable Long ownerId
    ) {
        return ResponseEntity.ok(
                projectOwnerService.getById(ownerId)
        );
    }
}
