package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCapitalPurchaseRequest;
import com.mohdiop.m3fundapi.dto.response.CapitalPurchaseResponse;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.Notification;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import com.mohdiop.m3fundapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CapitalPurchaseService {

    private final CapitalPurchaseRepository capitalPurchaseRepository;
    private final ContributorRepository contributorRepository;
    private final CampaignRepository campaignRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final NotificationRepository notificationRepository;

    public CapitalPurchaseService(CapitalPurchaseRepository capitalPurchaseRepository, ContributorRepository contributorRepository, CampaignRepository campaignRepository, ProjectOwnerRepository projectOwnerRepository, NotificationRepository notificationRepository) {
        this.capitalPurchaseRepository = capitalPurchaseRepository;
        this.contributorRepository = contributorRepository;
        this.campaignRepository = campaignRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public CapitalPurchaseResponse createCapitalPurchase(
            Long contributorId,
            Long campaignId,
            CreateCapitalPurchaseRequest createCapitalPurchaseRequest
    ) throws BadRequestException {
        var contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var campaign = campaignRepository.findById(campaignId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Campagne introuvable.")
                );
        if (campaign.getType() != CampaignType.INVESTMENT) {
            throw new BadRequestException("Cette campagne n'est pas une campagne d'investissement");
        }
        if (campaign.getShareOffered() < createCapitalPurchaseRequest.shareAcquired()) {
            throw new BadRequestException("La part ne doit pas être supérieure à la part disponible.");
        }

        var capitalPurchase = createCapitalPurchaseRequest.toCapitalPurchase();
        capitalPurchase.setContributor(contributor);
        capitalPurchase.setCampaign(campaign);
        sendCapitalPurchaseNotification(
                contributorId,
                campaign.getProjectOwner().getId(),
                createCapitalPurchaseRequest.shareAcquired(),
                createCapitalPurchaseRequest.payment().amount(),
                campaign.getProject().getName()
        );
        return capitalPurchaseRepository.save(capitalPurchase).toResponse();
    }

    public CapitalPurchaseResponse validateCapitalPurchaseByOwner(
            Long capitalPurchaseId
    ) throws BadRequestException {
        var capitalPurchase = capitalPurchaseRepository.findById(
                capitalPurchaseId
        ).orElseThrow(
                () -> new EntityNotFoundException("Achat de capital introuvable.")
        );
        if (capitalPurchase.isValidatedByProjectOwner()) {
            throw new BadRequestException("Achat de capital déjà validé.");
        }
        capitalPurchase.setValidatedByProjectOwner(true);
        return capitalPurchaseRepository.save(capitalPurchase).toResponse();
    }

    public void sendCapitalPurchaseNotification(
            Long contributorId,
            Long ownerId,
            double shareAcquired,
            double amount,
            String projectName
    ) {
        Contributor investor = contributorRepository.findById(contributorId).orElseThrow(
                () -> new EntityNotFoundException("Contributeur introuvable.")
        );
        var owner = projectOwnerRepository.findById(ownerId).orElseThrow(
                () -> new EntityNotFoundException("Projet introuvable.")
        );
        String title = "Nouveau achat de capital";
        String content = String.format(
                "%s %s a acheté %s pour %s FCFA de votre projet %s. Veuillez le valider.",
                investor.getFirstName(),
                investor.getLastName(),
                shareAcquired,
                amount,
                projectName
        );
        notificationRepository.save(
                new Notification(
                        null,
                        title,
                        content,
                        investor,
                        owner,
                        LocalDateTime.now(),
                        false,
                        NotificationType.NEW_CONTRIBUTION
                )
        );
    }

}
