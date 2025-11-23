package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.ValidationRequestProjectResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ValidationRequestService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/validations")
public class ValidationController {

    private final ValidationRequestService validationRequestService;
    private final AuthenticationService authenticationService;

    public ValidationController(ValidationRequestService validationRequestService, AuthenticationService authenticationService) {
        this.validationRequestService = validationRequestService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @PostMapping("/owners/{ownerId}/validate")
    public ResponseEntity<ValidationRequestOwnerResponse> validateOwner(
            @PathVariable Long ownerId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                validationRequestService.validateOwner(
                        authenticationService.getCurrentUserId(),
                        ownerId
                )
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @PostMapping("/owners/{ownerId}/refuse")
    public ResponseEntity<ValidationRequestOwnerResponse> refuseValidation(
            @PathVariable Long ownerId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                validationRequestService.refuseOwner(
                        authenticationService.getCurrentUserId(),
                        ownerId
                )
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @PostMapping("/projects/{projectId}/validate")
    public ResponseEntity<ValidationRequestProjectResponse> validateProject(
            @PathVariable Long projectId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                validationRequestService.validateProject(
                        authenticationService.getCurrentUserId(),
                        projectId
                )
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @PostMapping("/projects/{projectId}/refuse")
    public ResponseEntity<ValidationRequestProjectResponse> refuseProject(
            @PathVariable Long projectId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                validationRequestService.refuseProject(
                        authenticationService.getCurrentUserId(),
                        projectId
                )
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @GetMapping("/owners")
    public ResponseEntity<List<ValidationRequestOwnerResponse>> getAllPendingOwnersValidations() {
        return ResponseEntity.ok(
                validationRequestService.getAllPendingOwnersValidations()
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @GetMapping("/projects")
    public ResponseEntity<List<ValidationRequestProjectResponse>> getAllPendingProjectsValidations() {
        return ResponseEntity.ok(
                validationRequestService.getAllPendingProjectsValidations()
        );
    }
}
