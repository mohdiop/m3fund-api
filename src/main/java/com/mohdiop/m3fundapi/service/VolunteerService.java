package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.VolunteerResponse;
import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.Volunteer;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.repository.CampaignRepository;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import com.mohdiop.m3fundapi.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ContributorRepository contributorRepository;
    private final CampaignRepository campaignRepository;

    public VolunteerService(VolunteerRepository volunteerRepository, ContributorRepository contributorRepository, CampaignRepository campaignRepository) {
        this.volunteerRepository = volunteerRepository;
        this.contributorRepository = contributorRepository;
        this.campaignRepository = campaignRepository;
    }

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
        if (campaign.getType() != CampaignType.VOLUNTEERING) {
            throw new BadRequestException("C'est pas une campagne de volontariat.");
        }
        if (campaign.getTargetVolunteer() <= campaign.getCurrentVolunteerNumber()) {
            throw new BadRequestException("Nombre de volontaire atteint pour ce projet.");
        }
        if (volunteerRepository.findByContributorIdAndCampaignId(contributorId, campaignId).isPresent()) {
            throw new BadRequestException("Contributeur déjà volontaire pour ce projet.");
        }
        return volunteerRepository.save(
                Volunteer.builder()
                        .id(null)
                        .date(LocalDateTime.now())
                        .campaign(campaign)
                        .contributor(contributor)
                        .build()
        ).toResponse();
    }

    public boolean isVolunteerOfCampaign(
            Long contributorId,
            Long campaignId
    ) {
        return volunteerRepository.findByContributorIdAndCampaignId(contributorId, campaignId).isPresent();
    }
}
