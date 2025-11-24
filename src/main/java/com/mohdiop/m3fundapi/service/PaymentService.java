package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.entity.*;
import com.mohdiop.m3fundapi.entity.enums.*;
import com.mohdiop.m3fundapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    private final ContributorRepository contributorRepository;
    private final CampaignRepository campaignRepository;
    private final PaymentRepository paymentRepository;
    private final AdministratorRepository administratorRepository;
    private final ActionRepository actionRepository;
    private final SystemRepository systemRepository;
    private final EmailService emailService;

    public PaymentService(ContributorRepository contributorRepository, CampaignRepository campaignRepository, PaymentRepository paymentRepository, AdministratorRepository administratorRepository, ActionRepository actionRepository, SystemRepository systemRepository, EmailService emailService) {
        this.contributorRepository = contributorRepository;
        this.campaignRepository = campaignRepository;
        this.paymentRepository = paymentRepository;
        this.administratorRepository = administratorRepository;
        this.actionRepository = actionRepository;
        this.systemRepository = systemRepository;
        this.emailService = emailService;
    }

    public List<PaymentResponse> getAllContributorPayments(
            Long contributorId
    ) {
        Contributor contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        List<PaymentResponse> payments = new ArrayList<>();
        for (Gift gift : contributor.getGifts()) {
            payments.add(
                    gift.getPayment().toResponse()
            );
        }
        for (CapitalPurchase capitalPurchase : contributor.getCapitalPurchases()) {
            payments.add(
                    capitalPurchase.getPayment().toResponse()
            );
        }
        return payments;
    }

    public List<CampaignResponse> getFinishedCampaignsToDisbursed() {
        var campaignsToDisbursed = campaignRepository.findByState(CampaignState.FINISHED).stream()
                .filter(campaign -> campaign.getType() == CampaignType.DONATION
                        || campaign.getType() == CampaignType.INVESTMENT)
                .filter(campaign -> !campaign.isDisbursed())
                .map(Campaign::toResponse)
                .toList();
        if (campaignsToDisbursed.isEmpty()) return new ArrayList<>();
        return campaignsToDisbursed;
    }

    @Transactional
    public PaymentResponse disburse(
            Long authorId,
            Long campaignId
    ) throws BadRequestException {
        var campaign = campaignRepository.findById(campaignId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Campagne introuvable")
                );
        var author = administratorRepository.findById(authorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable")
                );
        if (campaign.getState() != CampaignState.FINISHED) {
            throw new BadRequestException("Décaissement impossible pour une campagne non terminée");
        }
        if (campaign.isDisbursed()) {
            throw new BadRequestException("Décaissement déjà effectué pour cette campagne");
        }
        Set<Payment> payments = new HashSet<>();
        switch (campaign.getType()) {
            case INVESTMENT -> {
                if (campaign.getCapitalPurchase() != null) {
                    payments.add(campaign.getCapitalPurchase().getPayment());
                }
            }
            case VOLUNTEERING ->
                    throw new BadRequestException("Impossible de faire un décaissement pour un campaigne de bénévolat.");
            case DONATION -> {
                for (Gift gift : campaign.getGifts()) {
                    payments.add(gift.getPayment());
                }
            }
        }
        var totalGained = 0D;
        for (Payment payment : payments) {
            totalGained += payment.getAmount();
        }
        double totalToDisbursed = (totalGained * 90) / 100;
        double m3fundFees = (totalGained * 10) / 100;

        var payment = paymentRepository.save(
                Payment.builder()
                        .id(null)
                        .amount(totalToDisbursed)
                        .state(PaymentState.SUCCESS)
                        .type(PaymentType.ORANGE_MONEY)
                        .carriedOutOn(LocalDateTime.now())
                        .strategy(PaymentStrategy.DISBURSED)
                        .transactionId(UUID.randomUUID().toString())
                        .build()
        );
        var system = systemRepository.findAll().getFirst();
        system.setFund(system.getFund() + m3fundFees);
        systemRepository.save(system);
        actionRepository.save(
                Action.builder()
                        .id(null)
                        .entityName(EntityName.PAYMENT)
                        .actionType(ActionType.DISBURSING)
                        .payment(payment)
                        .author(author)
                        .actionDate(LocalDateTime.now())
                        .build()
        );
        sendDisbursingMail(totalToDisbursed, m3fundFees, campaign);
        campaign.setDisbursed(true);
        campaign.getProjectOwner().setFund(
                campaign.getProjectOwner().getFund() + totalToDisbursed
        );
        campaignRepository.save(campaign);
        return payment.toResponse(campaign.getProject().getName());
    }

    public void sendDisbursingMail(
            Double disbursedAmount,
            Double fees,
            Campaign campaign
    ) {
        String subject = "Décaissement";
        String content = String.format(
                """
                        Un décaissement de %s FCFA a été effectué vers votre compte suite à la fin de la camapagne pour votre projet %s.
                        Un frais de %s FCFA a été prélévé du total (10 %% du total).
                        L'équipe de M3Fund.
                        """,
                disbursedAmount.toString(),
                campaign.getProject().getName(),
                fees.toString()
        );
        emailService.sendEmail(
                campaign.getProjectOwner().getEmail(),
                subject,
                content
        );
    }

}
