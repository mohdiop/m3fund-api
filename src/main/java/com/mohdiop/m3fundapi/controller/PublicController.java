package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {
        return ResponseEntity.ok(
                campaignService.getAllCampaign()
        );
    }
}
