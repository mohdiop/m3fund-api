package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.update.UpdateAssociationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateOrganizationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.OrganizationProjectOwnerResponse;
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
    @PatchMapping("/individuals")
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

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PatchMapping("/organizations")
    public ResponseEntity<OrganizationProjectOwnerResponse> updateOrganizationProjectOwner(
            @Valid UpdateOrganizationProjectOwnerRequest updateOrganizationProjectOwnerRequest
            ) throws BadRequestException {
        return ResponseEntity.ok(
                projectOwnerService.updateOrganizationProjectOwner(
                        authenticationService.getCurrentUserId(),
                        updateOrganizationProjectOwnerRequest
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PatchMapping("/associations")
    public ResponseEntity<AssociationProjectOwnerResponse> updateAssociationProjectOwner(
            @Valid UpdateAssociationProjectOwnerRequest updateAssociationProjectOwnerRequest
    ) throws BadRequestException {
        return ResponseEntity.ok(
                projectOwnerService.updateAssociationProjectOwner(
                        authenticationService.getCurrentUserId(),
                        updateAssociationProjectOwnerRequest
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
