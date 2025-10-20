package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.Reward;
import com.mohdiop.m3fundapi.repository.CampaignRepository;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final ProjectRepository projectRepository;

    public CampaignService(CampaignRepository campaignRepository, ProjectOwnerRepository projectOwnerRepository, ProjectRepository projectRepository) {
        this.campaignRepository = campaignRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.projectRepository = projectRepository;
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
}
