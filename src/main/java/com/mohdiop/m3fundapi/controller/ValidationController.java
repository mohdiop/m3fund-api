package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestResponse;
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
    public ResponseEntity<ValidationRequestResponse> validateOwner(
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
    public ResponseEntity<ValidationRequestResponse> refuseValidation(
            @PathVariable Long ownerId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                validationRequestService.refuseValidation(
                        authenticationService.getCurrentUserId(),
                        ownerId
                )
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'VALIDATIONS_ADMIN')")
    @GetMapping("/owners")
    public ResponseEntity<List<ValidationRequestResponse>> getAllPendingValidations() {
        return ResponseEntity.ok(
                validationRequestService.getAllPendingValidations()
        );
    }
}
