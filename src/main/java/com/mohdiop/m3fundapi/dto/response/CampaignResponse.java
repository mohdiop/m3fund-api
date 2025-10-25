package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;

import java.time.LocalDateTime;
import java.util.List;

public record CampaignResponse(
        Long id,
        ProjectResponse projectResponse,
        SimpleOwnerResponse owner,
        LocalDateTime launchedAt,
        LocalDateTime endAt,
        double targetBudget,
        long targetVolunteer,
        double shareOffered,
        CampaignType type,
        CampaignState state,
        List<RewardResponse> rewards,
        double currentFund,
        int numberOfVolunteer
) {
}
