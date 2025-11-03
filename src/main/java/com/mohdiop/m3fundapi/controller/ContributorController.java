package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.update.UpdateContributorRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.ContributionResponse;
import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.ContributionService;
import com.mohdiop.m3fundapi.service.ContributorService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contributors")
public class ContributorController {

    private final ContributorService contributorService;
    private final AuthenticationService authenticationService;
    private final ContributionService contributionService;
    private final CampaignService campaignService;

    public ContributorController(ContributorService contributorService, AuthenticationService authenticationService, ContributionService contributionService, CampaignService campaignService) {
        this.contributorService = contributorService;
        this.authenticationService = authenticationService;
        this.contributionService = contributionService;
        this.campaignService = campaignService;
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

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @GetMapping("/stats")
    public ResponseEntity<ContributionResponse> getMyStats() {
        return ResponseEntity.ok(
                contributionService.getAllContributorSContribution(authenticationService.getCurrentUserId())
        );
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @GetMapping("/recommended-campaigns")
    public ResponseEntity<List<CampaignResponse>> getMyRecommendedCampaigns() {
        return ResponseEntity.ok(
                campaignService.getContributorRecommendation(
                        authenticationService.getCurrentUserId()
                )
        );
    }
}
