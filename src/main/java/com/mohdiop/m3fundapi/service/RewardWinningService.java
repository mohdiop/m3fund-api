package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.RewardWinningResponse;
import com.mohdiop.m3fundapi.entity.RewardWinning;
import com.mohdiop.m3fundapi.repository.RewardWinningRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardWinningService {

    private final RewardWinningRepository rewardWinningRepository;

    public RewardWinningService(RewardWinningRepository rewardWinningRepository) {
        this.rewardWinningRepository = rewardWinningRepository;
    }

    public List<RewardWinningResponse> getMyRewards(
            Long contributorId
    ) {
        var userRewards = rewardWinningRepository.findByContributorId(contributorId);
        return userRewards.stream().map(RewardWinning::toResponse).toList();
    }
}
