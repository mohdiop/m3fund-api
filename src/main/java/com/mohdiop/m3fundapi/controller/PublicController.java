package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.CampaignDashboardResponse;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectStatsResponse;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final ProjectService projectService;
    private final CampaignService campaignService;

    public PublicController(ProjectService projectService, CampaignService campaignService) {
        this.projectService = projectService;
        this.campaignService = campaignService;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<OwnerProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(
                projectService.getAllProjects()
        );
    }

    @GetMapping("/projects/search")
    public ResponseEntity<List<OwnerProjectResponse>> searchProjects(
            @RequestParam("q") String searchTerm
    ) {
        return ResponseEntity.ok(
                projectService.searchProjects(searchTerm)
        );
    }

    @GetMapping("/projects/validated")
    public ResponseEntity<List<OwnerProjectResponse>> getValidatedProjects() {
        return ResponseEntity.ok(
                projectService.getValidatedProjects()
        );
    }

    @GetMapping("/projects/pending")
    public ResponseEntity<List<OwnerProjectResponse>> getPendingProjects() {
        return ResponseEntity.ok(
                projectService.getPendingProjects()
        );
    }

    @GetMapping("/projects/domain/{domain}")
    public ResponseEntity<List<OwnerProjectResponse>> getProjectsByDomain(
            @PathVariable String domain
    ) {
        return ResponseEntity.ok(
                projectService.filterByDomain(domain)
        );
    }

    @GetMapping("/projects/stats")
    public ResponseEntity<ProjectStatsResponse> getProjectStats() {
        return ResponseEntity.ok(
                projectService.getProjectStats()
        );
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {
        return ResponseEntity.ok(
                campaignService.getAllCampaign()
        );
    }

    @GetMapping("/campaigns/dashboard")
    public ResponseEntity<List<CampaignDashboardResponse>> getAllCampaignsForDashboard() {
        return ResponseEntity.ok(
                campaignService.getAllCampaignsForDashboard()
        );
    }

    @GetMapping("/campaigns/active")
    public ResponseEntity<List<CampaignDashboardResponse>> getActiveCampaigns() {
        return ResponseEntity.ok(
                campaignService.getActiveCampaigns()
        );
    }

    @GetMapping("/campaigns/search")
    public ResponseEntity<List<CampaignDashboardResponse>> searchCampaigns(
            @RequestParam("q") String searchTerm
    ) {
        return ResponseEntity.ok(
                campaignService.searchCampaigns(searchTerm)
        );
    }

    @GetMapping("/campaigns/project/{projectId}")
    public ResponseEntity<List<CampaignDashboardResponse>> getCampaignsByProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                campaignService.filterCampaignsByProject(projectId)
        );
    }

    @GetMapping("/campaigns/status/{status}")
    public ResponseEntity<List<CampaignDashboardResponse>> getCampaignsByStatus(
            @PathVariable String status
    ) {
        return ResponseEntity.ok(
                campaignService.filterCampaignsByStatus(status)
        );
    }

    @GetMapping("/campaigns/stats")
    public ResponseEntity<java.util.Map<String, Long>> getCampaignStats() {
        return ResponseEntity.ok(
                campaignService.getCampaignStats()
        );
    }
}
