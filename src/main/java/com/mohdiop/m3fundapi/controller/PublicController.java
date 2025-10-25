package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.CheckForEmailAndPhoneValidityRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.service.CampaignService;
import com.mohdiop.m3fundapi.service.DownloadService;
import com.mohdiop.m3fundapi.service.ProjectService;
import com.mohdiop.m3fundapi.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final ProjectService projectService;
    private final CampaignService campaignService;
    private final UserService userService;
    private final DownloadService downloadService;

    public PublicController(ProjectService projectService, CampaignService campaignService, UserService userService, DownloadService downloadService) {
        this.projectService = projectService;
        this.campaignService = campaignService;
        this.userService = userService;
        this.downloadService = downloadService;
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

    @PostMapping("/valid-email-and-phone")
    public ResponseEntity<?> checkForValidity(
            @Valid @RequestBody CheckForEmailAndPhoneValidityRequest checkForEmailAndPhoneValidityRequest
    ) throws BadRequestException {
        userService.checkForEmailAndPhoneValidity(checkForEmailAndPhoneValidityRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "absolutePath") String absolutePath
    ) {
        return downloadService.downloadByPath(absolutePath);
    }
}
