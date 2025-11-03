package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.Reward;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import com.mohdiop.m3fundapi.repository.CampaignRepository;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final ProjectRepository projectRepository;
    private final ContributorRepository contributorRepository;

    public CampaignService(CampaignRepository campaignRepository, ProjectOwnerRepository projectOwnerRepository, ProjectRepository projectRepository, ContributorRepository contributorRepository) {
        this.campaignRepository = campaignRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.projectRepository = projectRepository;
        this.contributorRepository = contributorRepository;
    }

    public CampaignResponse createCampaign(
            Long ownerId,
            Long projectId,
            CreateCampaignRequest createCampaignRequest
    ) throws AccessDeniedException {
        ProjectOwner owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Porteur introuvable.")
                );
        Project project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable.")
                );
        if (!Objects.equals(project.getOwner().getId(), owner.getId())) {
            throw new AccessDeniedException("Accès réfusé.");
        }
        Campaign campaign;
        switch (createCampaignRequest.type()) {
            case INVESTMENT -> campaign = createCampaignRequest.toInvestmentCampaign();
            case VOLUNTEERING -> campaign = createCampaignRequest.toVolunteeringCampaign();
            case DONATION -> {
                campaign = createCampaignRequest.toDonationCampaign();
                Set<Reward> rewards = new HashSet<>();
                for (var rewardRequest : createCampaignRequest.rewards()) {
                    rewards.add(rewardRequest.toReward());
                }
                for (var reward : rewards) {
                    reward.setCampaign(campaign);
                }
                campaign.setRewards(rewards);
            }
            default -> campaign = null;
        }
        campaign.setProjectOwner(owner);
        campaign.setProject(project);
        return campaignRepository.save(campaign).toResponse();
    }

    public List<CampaignResponse> getAllCampaign() {
        var allCampaigns = campaignRepository.findAll();
        return allCampaigns.stream().map(Campaign::toResponse).toList();
    }

    public List<CampaignResponse> getContributorRecommendation(
            Long contributorId
    ) {
        var contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable")
                );
        List<CampaignResponse> campaigns = new ArrayList<>();
        for (CampaignType campaignType : contributor.getCampaignTypes()) {
            var camps = campaignRepository.findByTypeAndState(campaignType, CampaignState.IN_PROGRESS);
            campaigns.addAll(camps.stream().map(Campaign::toResponse).toList());
        }
        List<Project> projects = new ArrayList<>();
        for (ProjectDomain projectDomain : contributor.getProjectDomains()) {
            var pro = projectRepository.findByDomainAndIsValidated(projectDomain, true);
            projects.addAll(pro);
        }
        for (Project project : projects) {
            if (!project.getCampaigns().isEmpty()) {
                campaigns.addAll(project.getCampaigns().stream().map(Campaign::toResponse).toList());
            }
        }
        return campaigns;
    }

    public List<ProjectResponse> getProjectsByAllCampaigns(
            List<Long> campaignsId
    ) {
        List<Campaign> campaigns = campaignRepository.findAllById(campaignsId);
        return campaigns.stream().map(campaign -> campaign.getProject().toResponse()).toList();
    }

    public List<CampaignResponse> getNewCampaigns() {
        List<Campaign> campaigns = campaignRepository.findByLaunchedAtAfterAndStateOrderByLaunchedAtDesc(LocalDateTime.now().minusWeeks(2), CampaignState.IN_PROGRESS);
        if (campaigns.isEmpty()) return new ArrayList<>();
        return campaigns.stream().map(Campaign::toResponse).toList();
    }
}
