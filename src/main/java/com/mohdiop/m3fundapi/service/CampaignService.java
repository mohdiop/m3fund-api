package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignDashboardResponse;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.Reward;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
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

    public List<CampaignDashboardResponse> getAllCampaignsForDashboard() {
        var allCampaigns = campaignRepository.findAll();
        return allCampaigns.stream()
                .map(Campaign::toDashboardResponse)
                .toList();
    }

    public List<CampaignDashboardResponse> getActiveCampaigns() {
        var allCampaigns = campaignRepository.findAll();
        return allCampaigns.stream()
                .filter(campaign -> campaign.getState() == CampaignState.IN_PROGRESS)
                .map(Campaign::toDashboardResponse)
                .toList();
    }

    public List<CampaignDashboardResponse> searchCampaigns(String searchTerm) {
        var allCampaigns = campaignRepository.findAll();
        String lowerSearchTerm = searchTerm.toLowerCase();
        return allCampaigns.stream()
                .filter(campaign -> 
                    campaign.getProject().getName().toLowerCase().contains(lowerSearchTerm) ||
                    campaign.getProject().getDescription().toLowerCase().contains(lowerSearchTerm)
                )
                .map(Campaign::toDashboardResponse)
                .toList();
    }

    public List<CampaignDashboardResponse> filterCampaignsByProject(Long projectId) {
        var allCampaigns = campaignRepository.findAll();
        return allCampaigns.stream()
                .filter(campaign -> campaign.getProject().getId().equals(projectId))
                .map(Campaign::toDashboardResponse)
                .toList();
    }

    public List<CampaignDashboardResponse> filterCampaignsByStatus(String status) {
        var allCampaigns = campaignRepository.findAll();
        CampaignState campaignState;
        try {
            campaignState = CampaignState.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list if invalid status
        }
        return allCampaigns.stream()
                .filter(campaign -> campaign.getState() == campaignState)
                .map(Campaign::toDashboardResponse)
                .toList();
    }

    public java.util.Map<String, Long> getCampaignStats() {
        var allCampaigns = campaignRepository.findAll();
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        
        stats.put("total", (long) allCampaigns.size());
        stats.put("inProgress", allCampaigns.stream()
                .filter(c -> c.getState() == CampaignState.IN_PROGRESS)
                .count());
        stats.put("pending", 0L); // Not available in current CampaignState enum
        stats.put("completed", allCampaigns.stream()
                .filter(c -> c.getState() == CampaignState.FINISHED)
                .count());
        stats.put("rejected", 0L); // Not available in current CampaignState enum
        
        return stats;
    }

    public CampaignResponse updateCampaign(
            Long ownerId,
            Long campaignId,
            UpdateCampaignRequest updateCampaignRequest
    ) throws AccessDeniedException {
        // Récupérer la campagne
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campagne introuvable."));

        // Vérifier que l'utilisateur est le propriétaire de la campagne
        if (!Objects.equals(campaign.getProjectOwner().getId(), ownerId)) {
            throw new AccessDeniedException("Accès réfusé.");
        }

        // Mettre à jour uniquement les champs non nuls et existants dans l'entité Campaign
        if (updateCampaignRequest.getDescription() != null && !updateCampaignRequest.getDescription().trim().isEmpty()) {
            campaign.setDescription(updateCampaignRequest.getDescription());
        }
        if (updateCampaignRequest.getTargetBudget() != null) {
            campaign.setTargetBudget(updateCampaignRequest.getTargetBudget());
        }
        if (updateCampaignRequest.getShareOffered() != null) {
            campaign.setShareOffered(updateCampaignRequest.getShareOffered());
        }
        if (updateCampaignRequest.getStartDate() != null) {
            campaign.setLaunchedAt(updateCampaignRequest.getStartDate());
        }
        if (updateCampaignRequest.getEndDate() != null) {
            campaign.setEndAt(updateCampaignRequest.getEndDate());
        }
        if (updateCampaignRequest.getTargetVolunteer() != null) {
            campaign.setTargetVolunteer(updateCampaignRequest.getTargetVolunteer());
        }

        return campaignRepository.save(campaign).toResponse();
    }
}
