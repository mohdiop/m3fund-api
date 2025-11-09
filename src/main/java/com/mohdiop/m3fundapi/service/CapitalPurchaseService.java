package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCapitalPurchaseRequest;
import com.mohdiop.m3fundapi.dto.response.CapitalPurchaseResponse;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.repository.CampaignRepository;
import com.mohdiop.m3fundapi.repository.CapitalPurchaseRepository;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class CapitalPurchaseService {

    private final CapitalPurchaseRepository capitalPurchaseRepository;
    private final ContributorRepository contributorRepository;
    private final CampaignRepository campaignRepository;

    public CapitalPurchaseService(CapitalPurchaseRepository capitalPurchaseRepository, ContributorRepository contributorRepository, CampaignRepository campaignRepository) {
        this.capitalPurchaseRepository = capitalPurchaseRepository;
        this.contributorRepository = contributorRepository;
        this.campaignRepository = campaignRepository;
    }

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
            throw new BadRequestException("La part ne doit pas être supérieure à la part disponible");
        }

        double dueAmount = (campaign.getTargetBudget() * createCapitalPurchaseRequest.shareAcquired()) / campaign.getShareOffered();
        if (((Double) dueAmount).intValue() != ((Double) createCapitalPurchaseRequest.payment().amount()).intValue()) {
            throw new BadRequestException("Cette somme est différente de la somme à devoir");
        }

        campaign.setTargetBudget(campaign.getTargetBudget() - createCapitalPurchaseRequest.payment().amount());
        campaign.setShareOffered(campaign.getShareOffered() - createCapitalPurchaseRequest.shareAcquired());
        var capitalPurchase = createCapitalPurchaseRequest.toCapitalPurchase();
        capitalPurchase.setContributor(contributor);
        capitalPurchase.setCampaign(campaign);
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

}
