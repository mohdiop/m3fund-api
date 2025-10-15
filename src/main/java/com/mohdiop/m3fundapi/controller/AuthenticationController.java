package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.AuthenticationRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateAssociationProjectOwner;
import com.mohdiop.m3fundapi.dto.request.create.CreateContributorRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.ContributorService;
import com.mohdiop.m3fundapi.service.ProjectOwnerService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ContributorService contributorService;
    private final ProjectOwnerService projectOwnerService;

    public AuthenticationController(AuthenticationService authenticationService, ContributorService contributorService, ProjectOwnerService projectOwnerService) {
        this.authenticationService = authenticationService;
        this.contributorService = contributorService;
        this.projectOwnerService = projectOwnerService;
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

    @PostMapping("/register/project-owners/individual")
    public ResponseEntity<IndividualProjectOwnerResponse> registerIndividualProjectOwner(
            @Valid @ModelAttribute CreateIndividualProjectOwnerRequest createIndividualProjectOwnerRequest
    ) throws BadRequestException {
        return new ResponseEntity<>(
                projectOwnerService.createIndividualProjectOwner(createIndividualProjectOwnerRequest),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/register/project-owners/association")
    public ResponseEntity<AssociationProjectOwnerResponse> registerAssociationProjectOwner(
            @Valid @ModelAttribute CreateAssociationProjectOwner createAssociationProjectOwner
    ) throws BadRequestException {
        return new ResponseEntity<>(
                projectOwnerService.createAssociationProjectOwner(createAssociationProjectOwner),
                HttpStatus.CREATED
        );
    }
}
