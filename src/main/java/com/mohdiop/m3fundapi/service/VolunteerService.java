package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.VolunteerResponse;
import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.Notification;
import com.mohdiop.m3fundapi.entity.Volunteer;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import com.mohdiop.m3fundapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ContributorRepository contributorRepository;
    private final CampaignRepository campaignRepository;
    private final NotificationRepository notificationRepository;
    private final ProjectOwnerRepository projectOwnerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository, ContributorRepository contributorRepository, CampaignRepository campaignRepository, NotificationRepository notificationRepository, ProjectOwnerRepository projectOwnerRepository) {
        this.volunteerRepository = volunteerRepository;
        this.contributorRepository = contributorRepository;
        this.campaignRepository = campaignRepository;
        this.notificationRepository = notificationRepository;
        this.projectOwnerRepository = projectOwnerRepository;
    }

    @Transactional
    public VolunteerResponse createVolunteer(
            Long contributorId,
            Long campaignId
    ) throws BadRequestException {
        Contributor contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Contributeur introuvable.")
                );
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Campagne introuvable.")
                );
        if (campaign.getState() == CampaignState.FINISHED) {
            throw new BadRequestException("Cette campagne est terminée.");
        }
        if (campaign.getType() != CampaignType.VOLUNTEERING) {
            throw new BadRequestException("C'est pas une campagne de volontariat.");
        }
        if (campaign.getTargetVolunteer() <= campaign.getCurrentVolunteerNumber()) {
            throw new BadRequestException("Nombre de volontaire atteint pour ce projet.");
        }
        if (volunteerRepository.findByContributorIdAndCampaignId(contributorId, campaignId).isPresent()) {
            throw new BadRequestException("Contributeur déjà volontaire pour ce projet.");
        }
        sendVolunteeringNotification(
                contributorId,
                campaign.getProjectOwner().getId(),
                campaign.getProject().getName()
        );
        return volunteerRepository.save(
                Volunteer.builder()
                        .id(null)
                        .date(LocalDateTime.now())
                        .campaign(campaign)
                        .contributor(contributor)
                        .build()
        ).toResponse();
    }

    public void isVolunteerOfCampaign(
            Long contributorId,
            Long campaignId
    ) {
        volunteerRepository.findByContributorIdAndCampaignId(contributorId, campaignId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Contributeur non volontaire pour ce projet.")
                );
    }

    private void sendVolunteeringNotification(
            Long volunteerId,
            Long ownerId,
            String projectName
    ) {
        Contributor volunteer = contributorRepository.findById(volunteerId).orElseThrow(
                () -> new EntityNotFoundException("Contributeur introuvable.")
        );
        var owner = projectOwnerRepository.findById(ownerId).orElseThrow(
                () -> new EntityNotFoundException("Projet introuvable.")
        );
        String title = "Nouveau volontaire";
        String content = String.format(
                "%s %s s'est porté(e) volontaire pour votre projet %s.",
                volunteer.getFirstName(),
                volunteer.getLastName(),
                projectName
        );
        notificationRepository.save(
                new Notification(
                        null,
                        title,
                        content,
                        volunteer,
                        owner,
                        LocalDateTime.now(),
                        false,
                        NotificationType.NEW_CONTRIBUTION
                )
        );
    }
}
