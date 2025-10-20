package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.RewardWinning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardWinningRepository extends JpaRepository<RewardWinning, Long> {
    Optional<RewardWinning> findByContributorIdAndRewardId(Long contributorId, Long rewardId);

    List<RewardWinning> findByContributorId(Long contributorId);
}
