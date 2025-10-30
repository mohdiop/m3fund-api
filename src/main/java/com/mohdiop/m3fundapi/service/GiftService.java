package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateGiftRequest;
import com.mohdiop.m3fundapi.dto.response.GiftResponse;
import com.mohdiop.m3fundapi.dto.response.TransactionResponse;
import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.entity.*;
import com.mohdiop.m3fundapi.entity.enums.RewardWinningState;
import com.mohdiop.m3fundapi.repository.CampaignRepository;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import com.mohdiop.m3fundapi.repository.GiftRepository;
import com.mohdiop.m3fundapi.repository.RewardWinningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GiftService {

    private final GiftRepository giftRepository;
    private final CampaignRepository campaignRepository;
    private final ContributorRepository contributorRepository;
    private final RewardWinningRepository rewardWinningRepository;

    public GiftService(GiftRepository giftRepository, CampaignRepository campaignRepository, ContributorRepository contributorRepository, RewardWinningRepository rewardWinningRepository) {
        this.giftRepository = giftRepository;
        this.campaignRepository = campaignRepository;
        this.contributorRepository = contributorRepository;
        this.rewardWinningRepository = rewardWinningRepository;
    }

    @Transactional
    public GiftResponse createGift(
            Long contributorId,
            Long campaignId,
            CreateGiftRequest createGiftRequest
    ) {
        Contributor contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Contributeur introuvable.")
                );
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Campagne introuvable.")
                );
        Gift gift = createGiftRequest.toGift();
        if (!campaign.getRewards().isEmpty()) {
            for (var reward : campaign.getRewards()) {
                winReward(contributor, reward, gift.getPayment().getAmount(), reward.getUnlockAmount());
            }
        }
        gift.setContributor(contributor);
        gift.setCampaign(campaign);
        return giftRepository.save(
                gift
        ).toResponse();
    }

    private void winReward(
            Contributor contributor,
            Reward reward,
            double paymentAmount,
            double unlockAmount
    ) {
        if (paymentAmount < unlockAmount) {
            return;
        }
        if (rewardWinningRepository.findByContributorIdAndRewardId(
                contributor.getId(),
                reward.getId()
        ).isPresent()) {
            return;
        }
        rewardWinningRepository.save(
                new RewardWinning(
                        null,
                        LocalDateTime.now(),
                        RewardWinningState.GAINED,
                        contributor,
                        reward
                )
        );
    }

    public List<GiftResponse> getMyGifts(Long contributorId) {
        List<Gift> gifts = giftRepository.findByContributorIdOrderByDateDesc(contributorId);
        return gifts.stream().map(Gift::toResponse).toList();
    }

    public List<TransactionResponse> getMyTransactions(Long userId) {
        // Essayer d'abord de récupérer en tant que contributeur
        List<Gift> gifts = giftRepository.findByContributorIdOrderByDateDesc(userId);
        
        // Si aucun gift trouvé en tant que contributeur, essayer en tant que project owner
        if (gifts.isEmpty()) {
            gifts = giftRepository.findByProjectOwnerIdOrderByDateDesc(userId);
        }
        
        return gifts.stream().map(gift -> {
            Campaign campaign = gift.getCampaign();
            Project project = campaign.getProject();
            
            return new TransactionResponse(
                    gift.getId(),
                    gift.getDate(),
                    gift.getPayment().toResponse(),
                    campaign.getId(),
                    "Campagne de " + campaign.getType().toString().toLowerCase(),
                    project.getName(),
                    project.getDomain().toString(),
                    project.getDescription()
            );
        }).toList();
    }
}
