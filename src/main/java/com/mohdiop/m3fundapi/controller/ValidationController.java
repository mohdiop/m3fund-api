package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ValidationRequestService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/owners/{ownerId}")
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
}
