package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.AuthenticationRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateContributorRequest;
import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ContributorService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ContributorService contributorService;

    public AuthenticationController(AuthenticationService authenticationService, ContributorService contributorService) {
        this.authenticationService = authenticationService;
        this.contributorService = contributorService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationService.TokenPairResponse> login(
            @Valid @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(
                authenticationService.authenticate(authenticationRequest)
        );
    }

    @PostMapping("/register/contributors")
    public ResponseEntity<ContributorResponse> registerContributor(
            @Valid @RequestBody CreateContributorRequest createContributorRequest
    ) throws BadRequestException {
        return new ResponseEntity<>(
                contributorService.createContributor(createContributorRequest),
                HttpStatus.CREATED
        );
    }
}
