package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateCapitalPurchaseRequest;
import com.mohdiop.m3fundapi.dto.response.CapitalPurchaseResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CapitalPurchaseService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/campaigns")
public class CapitalPurchaseController {

    private final CapitalPurchaseService capitalPurchaseService;
    private final AuthenticationService authenticationService;

    public CapitalPurchaseController(CapitalPurchaseService capitalPurchaseService, AuthenticationService authenticationService) {
        this.capitalPurchaseService = capitalPurchaseService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @PostMapping("/{campaignId}/capital-purchases")
    public ResponseEntity<CapitalPurchaseResponse> createCapitalPurchase(
            @PathVariable Long campaignId,
            @RequestBody @Valid CreateCapitalPurchaseRequest createCapitalPurchaseRequest
    ) throws BadRequestException {
        return new ResponseEntity<>(
                capitalPurchaseService.createCapitalPurchase(
                        authenticationService.getCurrentUserId(),
                        campaignId,
                        createCapitalPurchaseRequest
                ),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PostMapping("/capital-purchases/{capitalPurchaseId}/validate")
    public ResponseEntity<CapitalPurchaseResponse> validateCapitalPurchase(
            @PathVariable Long capitalPurchaseId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                capitalPurchaseService.validateCapitalPurchaseByOwner(
                        capitalPurchaseId
                )
        );
    }
}
