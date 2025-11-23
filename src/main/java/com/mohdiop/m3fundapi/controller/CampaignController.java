package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.PaymentService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class CampaignController {

    private final CampaignService campaignService;
    private final PaymentService paymentService;
    private final AuthenticationService authenticationService;

    public CampaignController(CampaignService campaignService, PaymentService paymentService, AuthenticationService authenticationService) {
        this.campaignService = campaignService;
        this.paymentService = paymentService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PostMapping("/{projectId}/campaigns")
    public ResponseEntity<CampaignResponse> createCampaign(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateCampaignRequest createCampaignRequest
    ) throws AccessDeniedException, BadRequestException {
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
    @PatchMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long campaignId,
            @Valid @RequestBody UpdateCampaignRequest updateCampaignRequest
    ) {
        return ResponseEntity.ok(
                campaignService.updateCampaign(
                        authenticationService.getCurrentUserId(),
                        campaignId,
                        updateCampaignRequest
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @PostMapping("/campaigns/{campaignId}/finish")
    public ResponseEntity<CampaignResponse> finishCampaign(
            @PathVariable Long campaignId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                campaignService.finishCampaign(
                        authenticationService.getCurrentUserId(),
                        campaignId
                )
        );
    }

    @PreAuthorize("hasAnyRole('SYSTEM', 'SUPER_ADMIN', 'PAYMENTS_ADMIN')")
    @PostMapping("/campaigns/{campaignId}/disburse")
    public ResponseEntity<PaymentResponse> disburseCampaign(
            @PathVariable Long campaignId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                paymentService.disburse(
                        authenticationService.getCurrentUserId(),
                        campaignId
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/mine")
    public ResponseEntity<List<CampaignResponse>> getMyCampaigns() {
        return ResponseEntity.ok(
                campaignService.getMyCampaigns(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/mine-active")
    public ResponseEntity<List<CampaignResponse>> getMyActiveCampaigns() {
        return ResponseEntity.ok(
                campaignService.getActiveCampaigns(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/mine-finished")
    public ResponseEntity<List<CampaignResponse>> getMyFinishedCampaigns() {
        return ResponseEntity.ok(
                campaignService.getFinishedCampaigns(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/{projectId}/campaigns")
    public ResponseEntity<List<CampaignResponse>> getByProjectId(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                campaignService.getCampaignsByOwnerIdAndProjectId(
                        authenticationService.getCurrentUserId(),
                        projectId
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/search")
    public ResponseEntity<List<CampaignResponse>> searchCampaigns(
            @RequestParam("q") String searchTerm
    ) {
        return ResponseEntity.ok(
                campaignService.searchByTerm(
                        authenticationService.getCurrentUserId(),
                        searchTerm
                )
        );
    }

    @PreAuthorize("hasRole('PROJECT_OWNER')")
    @GetMapping("/campaigns/stats")
    public ResponseEntity<Map<String, Long>> getMyCampaignsStats(
    ) {
        return ResponseEntity.ok(
                campaignService.getCampaignsStats(
                        authenticationService.getCurrentUserId()
                )
        );
    }
}
