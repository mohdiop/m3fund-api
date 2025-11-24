package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCampaignRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateCampaignRequest;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.PendingDisburseCampaignResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.entity.*;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import com.mohdiop.m3fundapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final ProjectRepository projectRepository;
    private final ContributorRepository contributorRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public CampaignService(CampaignRepository campaignRepository, ProjectOwnerRepository projectOwnerRepository, ProjectRepository projectRepository, ContributorRepository contributorRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.campaignRepository = campaignRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.projectRepository = projectRepository;
        this.contributorRepository = contributorRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public CampaignResponse createCampaign(
            Long ownerId,
            Long projectId,
            CreateCampaignRequest createCampaignRequest
    ) throws AccessDeniedException, BadRequestException {
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
        var userCampaigns = campaignRepository.findByProjectOwnerId(ownerId);
        if (!canCreateCampaignThisWeek(userCampaigns, LocalDateTime.now())) {
            throw new BadRequestException("Vous ne pouvez créer plus de 2 campagnes par semaine.");
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

    public List<CampaignResponse> getAllCampaigns() {
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
        Set<CampaignResponse> campaigns = new HashSet<>();
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
        return campaigns.stream().toList();
    }

    public List<ProjectResponse> getProjectsByAllCampaigns(
            List<Long> campaignsId
    ) {
        List<Campaign> campaigns = campaignRepository.findAllById(campaignsId);
        return campaigns.stream().map(campaign -> campaign.getProject().toResponse()).toList();
    }

    public List<CampaignResponse> getNewCampaigns() {
        List<Campaign> campaigns = campaignRepository.findByLaunchedAtAfterAndStateOrderByLaunchedAtDesc(
                LocalDateTime.now().minusWeeks(2), CampaignState.IN_PROGRESS
        );
        if (campaigns.isEmpty()) return new ArrayList<>();
        return campaigns.stream().map(Campaign::toResponse).toList();
    }

    public CampaignResponse updateCampaign(
            Long demanderId,
            Long campaignId,
            UpdateCampaignRequest updateCampaignRequest
    ) throws AccessDeniedException {
        var demander = projectOwnerRepository.findById(demanderId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        var campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campagne introuvable."));

        if (!Objects.equals(demander.getId(), campaign.getProject().getOwner().getId())) {
            throw new AccessDeniedException("Accès refusé.");
        }

        if (updateCampaignRequest.endAt() != null) {
            if (updateCampaignRequest.endAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La date de fin doit être ultérieure à la date actuelle.");
            }
            campaign.setEndAt(updateCampaignRequest.endAt());
            campaign.setState(CampaignState.IN_PROGRESS);
        }

        if (updateCampaignRequest.type() != null) {
            campaign.setType(updateCampaignRequest.type());
        }

        if (updateCampaignRequest.targetBudget() != null) {
            if (updateCampaignRequest.targetBudget() < 0) {
                throw new IllegalArgumentException("Le budget cible doit être positif.");
            }
            campaign.setTargetBudget(updateCampaignRequest.targetBudget());
        }

        if (updateCampaignRequest.targetVolunteer() != null) {
            if (updateCampaignRequest.targetVolunteer() < 0) {
                throw new IllegalArgumentException("Le nombre de volontaires cibles doit être positif.");
            }
            campaign.setTargetVolunteer(updateCampaignRequest.targetVolunteer());
        }

        if (updateCampaignRequest.shareOffered() != null) {
            double share = updateCampaignRequest.shareOffered();
            if (share < 0 || share > 100) {
                throw new IllegalArgumentException("La part offerte doit être comprise entre 0 et 100.");
            }
            campaign.setShareOffered(share);
        }

        if (updateCampaignRequest.rewards() != null) {
            campaign.getRewards().clear();
            campaign.getRewards().addAll(
                    updateCampaignRequest.rewards().stream()
                            .map((r) -> {
                                var reward = r.toReward();
                                reward.setCampaign(campaign);
                                return reward;
                            })
                            .toList()
            );
        }

        return campaignRepository.save(campaign).toResponse();
    }

    public List<CampaignResponse> getMyCampaigns(
            Long ownerId
    ) {
        var campaigns = campaignRepository.findByProjectOwnerId(ownerId);
        if (campaigns.isEmpty()) return new ArrayList<>();
        return campaigns.stream().map(Campaign::toResponse).toList();
    }

    public List<CampaignResponse> getActiveCampaigns(
            Long ownerId
    ) {
        var campaigns = campaignRepository.findByProjectOwnerIdAndState(ownerId, CampaignState.IN_PROGRESS);
        if (campaigns.isEmpty()) return new ArrayList<>();
        return campaigns.stream().map(Campaign::toResponse).toList();
    }

    public List<CampaignResponse> getFinishedCampaigns(
            Long ownerId
    ) {
        var campaigns = campaignRepository.findByProjectOwnerIdAndState(ownerId, CampaignState.FINISHED);
        if (campaigns.isEmpty()) return new ArrayList<>();
        return campaigns.stream().map(Campaign::toResponse).toList();
    }

    public List<CampaignResponse> getCampaignsByOwnerIdAndProjectId(
            Long ownerId,
            Long projectId
    ) {
        var owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable.")
                );
        if (!Objects.equals(project.getOwner().getId(), owner.getId())) {
            throw new AccessDeniedException("Accès réfusé.");
        }
        var campaigns = campaignRepository.findByProjectOwnerIdAndProjectId(ownerId, projectId);
        if (campaigns.isEmpty()) return new ArrayList<>();
        return campaigns.stream().map(Campaign::toResponse).toList();
    }

    public List<CampaignResponse> searchByTerm(
            Long ownerId,
            String searchTerm
    ) {
        var projects = projectRepository.searchOwnerProjects(ownerId, searchTerm);
        if (projects.isEmpty()) return new ArrayList<>();
        var campaignProjects = projects.stream()
                .filter(project -> !project.getCampaigns().isEmpty()).toList();
        var campaigns = new ArrayList<CampaignResponse>();
        for (Project campaignProject : campaignProjects) {
            campaigns.addAll(
                    campaignProject.getCampaigns().stream().map(Campaign::toResponse).toList()
            );
        }
        return campaigns;
    }

    public Map<String, Long> getCampaignsStats(
            Long ownerId
    ) {
        var campaigns = campaignRepository.findByProjectOwnerId(ownerId);
        var stats = new HashMap<String, Long>();
        if (campaigns.isEmpty()) {
            stats.put("total", 0L);
            stats.put("inProgress", 0L);
            stats.put("finished", 0L);
            return stats;
        }
        stats.put("total", (long) campaigns.size());
        stats.put("inProgress", campaigns.stream().filter(campaign -> campaign.getState() == CampaignState.IN_PROGRESS).count());
        stats.put("finished", campaigns.stream().filter(campaign -> campaign.getState() == CampaignState.FINISHED).count());
        return stats;
    }

    public List<PendingDisburseCampaignResponse> getPendingForDisburseCampaign() {
        return campaignRepository.findByState(CampaignState.FINISHED)
                .stream().filter(campaign -> !campaign.isDisbursed())
                .map(Campaign::toPendingForDisbursingResponse)
                .filter(p -> p.amountToDisburse() > 0D)
                .toList();
    }

    @Transactional
    public CampaignResponse finishCampaign(
            Long authorId,
            Long campaignId
    ) throws BadRequestException {
        var campaign = campaignRepository.findById(campaignId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Campagne introuvable.")
                );
        if (!Objects.equals(campaign.getProjectOwner().getId(), authorId)) {
            throw new AccessDeniedException("Accès interdit à cette ressource");
        }
        if (campaign.getState() == CampaignState.FINISHED) {
            throw new BadRequestException("Cette campagne est déjà terminée.");
        }
        campaign.setState(CampaignState.FINISHED);
        campaign.setEndAt(LocalDateTime.now());
        switch (campaign.getType()) {
            case INVESTMENT -> sendCampaignFinishedNotificationToContributors(
                    new ArrayList<>(
                            Collections.singleton(campaign.getCapitalPurchase().getContributor())
                    ), campaign.getProject().getName()
            );
            case VOLUNTEERING -> sendCampaignFinishedNotificationToContributors(
                    campaign.getVolunteers().stream().map(Volunteer::getContributor).collect(Collectors.toSet()).stream().toList(),
                    campaign.getProject().getName()
            );
            case DONATION -> sendCampaignFinishedNotificationToContributors(
                    campaign.getGifts().stream().map(Gift::getContributor).collect(Collectors.toSet()).stream().toList(),
                    campaign.getProject().getName()
            );
        }
        return campaignRepository.save(campaign).toResponse();
    }

    public void finishCampaigns() {
        var allCampaigns = campaignRepository.findByState(CampaignState.IN_PROGRESS);
        for (Campaign campaign : allCampaigns) {
            if (campaign.getEndAt().isBefore(LocalDateTime.now())) {
                campaign.setState(CampaignState.FINISHED);
                campaignRepository.save(campaign);
                sendCampaignFinishedNotification(campaign.getProjectOwner().getId(), campaign.getProject().getName());
                switch (campaign.getType()) {
                    case INVESTMENT -> {
                        if (campaign.getCapitalPurchase() != null) {
                            sendCampaignFinishedNotificationToContributors(
                                    new ArrayList<>(
                                            Collections.singleton(campaign.getCapitalPurchase().getContributor())
                                    ), campaign.getProject().getName()
                            );
                        }
                    }
                    case VOLUNTEERING -> sendCampaignFinishedNotificationToContributors(
                            campaign.getVolunteers().stream().map(Volunteer::getContributor).collect(Collectors.toSet()).stream().toList(),
                            campaign.getProject().getName()
                    );
                    case DONATION -> sendCampaignFinishedNotificationToContributors(
                            campaign.getGifts().stream().map(Gift::getContributor).collect(Collectors.toSet()).stream().toList(),
                            campaign.getProject().getName()
                    );
                }
            }
        }
    }

    public void sendCampaignFinishedNotification(
            Long ownerId,
            String projectName
    ) {
        var system = userRepository.findById(1L)
                .orElseThrow(
                        () -> new RuntimeException("Un problème interne est survenu.")
                );
        var owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Porteur introuvable.")
                );
        String title = "Campagne terminée";
        String content = String.format(
                "La campagne pour votre projet %s vient de terminer.",
                projectName
        );
        notificationRepository.save(
                new Notification(
                        null,
                        title,
                        content,
                        system,
                        owner,
                        LocalDateTime.now(),
                        false,
                        NotificationType.CAMPAIGN_FINISHED
                )
        );
    }

    public void sendCampaignFinishedNotificationToContributors(
            List<Contributor> contributors,
            String projectName
    ) {
        var system = userRepository.findById(1L)
                .orElseThrow(
                        () -> new RuntimeException("Un problème interne est survenu.")
                );
        String title = "Campagne terminée";
        String content = String.format(
                "La campagne pour le projet %s auquel vous avez contribué vient de terminer.",
                projectName
        );
        notificationRepository.saveAll(
                contributors.stream().map(
                        contributor ->
                                new Notification(
                                        null,
                                        title,
                                        content,
                                        system,
                                        contributor,
                                        LocalDateTime.now(),
                                        false,
                                        NotificationType.CAMPAIGN_FINISHED
                                )
                ).collect(Collectors.toSet())
        );
    }

    /**
     * Vérifie si une nouvelle campagne peut être créée cette semaine
     *
     * @param campaigns   la liste des campagnes existantes
     * @param dateToCheck la date à laquelle on veut créer la campagne (ex: maintenant)
     * @return true si moins de 2 campagnes sont déjà créées cette semaine, false sinon
     */
    private boolean canCreateCampaignThisWeek(List<Campaign> campaigns, LocalDateTime dateToCheck) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = dateToCheck.get(weekFields.weekOfWeekBasedYear());
        int year = dateToCheck.getYear();

        long countThisWeek = campaigns.stream()
                .filter(c -> {
                    LocalDateTime launch = c.getLaunchedAt();
                    int campaignWeek = launch.get(weekFields.weekOfWeekBasedYear());
                    int campaignYear = launch.getYear();
                    return campaignWeek == weekNumber && campaignYear == year;
                })
                .count();

        return countThisWeek < 2;
    }

}
