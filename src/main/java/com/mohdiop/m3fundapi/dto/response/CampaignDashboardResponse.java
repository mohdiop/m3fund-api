package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;

import java.time.LocalDateTime;

public record CampaignDashboardResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        double targetBudget,
        double shareOffered,
        LocalDateTime startDate,
        LocalDateTime endDate,
        CampaignType campaignType,
        CampaignState status,
        double progress,
        double fundsRaised,
        int collaboratorCount,
        int campaignCount,
        double netValue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

