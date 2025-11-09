package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignDashboardResponse;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

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
    ) throws AccessDeniedException, org.apache.coyote.BadRequestException {
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
    ) throws AccessDeniedException, org.apache.coyote.BadRequestException {
        return ResponseEntity.ok(
                campaignService.updateCampaign(
                        authenticationService.getCurrentUserId(),
                        campaignId,
                        updateCampaignRequest
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/my-campaigns")
    public ResponseEntity<List<CampaignDashboardResponse>> getMyCampaigns() {
        return ResponseEntity.ok(
                campaignService.getCampaignsByOwner(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/my-campaigns/stats")
    public ResponseEntity<Map<String, Long>> getMyCampaignStats() {
        return ResponseEntity.ok(
                campaignService.getCampaignStatsByOwner(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/my-campaigns/search")
    public ResponseEntity<List<CampaignDashboardResponse>> searchMyCampaigns(
            @RequestParam("q") String searchTerm
    ) {
        return ResponseEntity.ok(
                campaignService.searchCampaignsByOwner(
                        authenticationService.getCurrentUserId(),
                        searchTerm
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/my-campaigns/project/{projectId}")
    public ResponseEntity<List<CampaignDashboardResponse>> getMyCampaignsByProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                campaignService.filterCampaignsByOwnerAndProject(
                        authenticationService.getCurrentUserId(),
                        projectId
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/my-campaigns/status/{status}")
    public ResponseEntity<List<CampaignDashboardResponse>> getMyCampaignsByStatus(
            @PathVariable String status
    ) {
        return ResponseEntity.ok(
                campaignService.filterCampaignsByOwnerAndStatus(
                        authenticationService.getCurrentUserId(),
                        status
                )
        );
    }
}
