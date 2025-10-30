package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.update.UpdateContributorRequest;
import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ContributorService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contributors")
public class ContributorController {

    private final ContributorService contributorService;
    private final AuthenticationService authenticationService;

    public ContributorController(ContributorService contributorService, AuthenticationService authenticationService) {
        this.contributorService = contributorService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @PatchMapping
    public ResponseEntity<ContributorResponse> patchContributor(
            @Valid @RequestBody UpdateContributorRequest updateContributorRequest
            ) throws BadRequestException {
        return ResponseEntity.ok(
                contributorService.updateContributor(
                        authenticationService.getCurrentUserId(),
                        updateContributorRequest
                )
        );
    }
}
