package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
public class CampaignController {

    private final CampaignService campaignService;
    private final AuthenticationService authenticationService;

    public CampaignController(CampaignService campaignService, AuthenticationService authenticationService) {
        this.campaignService = campaignService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PostMapping("/projects/{projectId}/campaigns")
    public ResponseEntity<CampaignResponse> createCampaign(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateCampaignRequest createCampaignRequest
    ) throws AccessDeniedException {
        return new ResponseEntity<>(
                campaignService.createCampaign(
                        authenticationService.getCurrentUserId(),
                        projectId,
                        createCampaignRequest
                ),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PutMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long campaignId,
            @Valid @RequestBody UpdateCampaignRequest updateCampaignRequest
    ) throws AccessDeniedException {
        return ResponseEntity.ok(
                campaignService.updateCampaign(
                        authenticationService.getCurrentUserId(),
                        campaignId,
                        updateCampaignRequest
                )
        );
    }
}
