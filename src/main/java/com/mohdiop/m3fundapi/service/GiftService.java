package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateGiftRequest;
import com.mohdiop.m3fundapi.dto.response.GiftResponse;
import com.mohdiop.m3fundapi.dto.response.RewardWinningResponse;
import com.mohdiop.m3fundapi.entity.*;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import com.mohdiop.m3fundapi.entity.enums.RewardWinningState;
import com.mohdiop.m3fundapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GiftService {

    private final GiftRepository giftRepository;
    private final CampaignRepository campaignRepository;
    private final ContributorRepository contributorRepository;
    private final RewardWinningRepository rewardWinningRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final NotificationRepository notificationRepository;

    public GiftService(GiftRepository giftRepository, CampaignRepository campaignRepository, ContributorRepository contributorRepository, RewardWinningRepository rewardWinningRepository, ProjectOwnerRepository projectOwnerRepository, NotificationRepository notificationRepository) {
        this.giftRepository = giftRepository;
        this.campaignRepository = campaignRepository;
        this.contributorRepository = contributorRepository;
        this.rewardWinningRepository = rewardWinningRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public GiftResponse createGift(
            Long contributorId,
            Long campaignId,
            CreateGiftRequest createGiftRequest
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
            throw new BadRequestException("Cette campagne terminée.");
        }
        Gift gift = createGiftRequest.toGift();
        gift.setContributor(contributor);
        gift.setCampaign(campaign);
        List<RewardWinningResponse> gainedRewards = new ArrayList<>();
        if (!campaign.getRewards().isEmpty()) {
            for (var reward : campaign.getRewards()) {
                if (reward.getQuantity() > 0) {
                    var rewardGained = winReward(contributor, reward, gift.getPayment().getAmount(), reward.getUnlockAmount());
                    if (rewardGained != null) {
                        gainedRewards.add(rewardGained);
                    }
                }
            }
        }
        sendContributionNotification(
                contributorId,
                campaign.getProjectOwner().getId(),
                createGiftRequest.payment().amount(),
                campaign.getProject().getName()
        );
        return giftRepository.save(
                gift
        ).toResponse(gainedRewards);
    }

    private RewardWinningResponse winReward(
            Contributor contributor,
            Reward reward,
            double paymentAmount,
            double unlockAmount
    ) {
        if (paymentAmount < unlockAmount) {
            return null;
        }
        if (rewardWinningRepository.findByContributorIdAndRewardId(
                contributor.getId(),
                reward.getId()
        ).isPresent()) {
            return null;
        }
        reward.setQuantity(reward.getQuantity() - 1);
        return rewardWinningRepository.save(
                new RewardWinning(
                        null,
                        LocalDateTime.now(),
                        RewardWinningState.GAINED,
                        contributor,
                        reward
                )
        ).toResponse();
    }


    private void sendContributionNotification(
            Long contributorId,
            Long ownerId,
            double contributedAmount,
            String projectName
    ) {
        Contributor contributor = contributorRepository.findById(contributorId).orElseThrow(
                () -> new EntityNotFoundException("Contributeur introuvable.")
        );
        var owner = projectOwnerRepository.findById(ownerId).orElseThrow(
                () -> new EntityNotFoundException("Projet introuvable.")
        );
        String title = "Nouvelle contribution";
        String content = String.format(
                "%s %s a contributé %s FCFA dans votre projet %s.",
                contributor.getFirstName(),
                contributor.getLastName(),
                contributedAmount,
                projectName
        );
        notificationRepository.save(
                new Notification(
                        null,
                        title,
                        content,
                        contributor,
                        owner,
                        LocalDateTime.now(),
                        false,
                        NotificationType.NEW_CONTRIBUTION
                )
        );
    }
}
