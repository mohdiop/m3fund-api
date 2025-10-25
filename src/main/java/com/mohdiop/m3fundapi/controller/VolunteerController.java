package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.VolunteerResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.VolunteerService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/campaigns")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final AuthenticationService authenticationService;

    public VolunteerController(VolunteerService volunteerService, AuthenticationService authenticationService) {
        this.volunteerService = volunteerService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @PostMapping("/{campaignId}/volunteers")
    public ResponseEntity<VolunteerResponse> createVolunteer(
            @PathVariable Long campaignId
    ) throws BadRequestException {
        return new ResponseEntity<>(
                volunteerService.createVolunteer(
                        authenticationService.getCurrentUserId(),
                        campaignId
                ),
                HttpStatus.CREATED
        );
    }
}
