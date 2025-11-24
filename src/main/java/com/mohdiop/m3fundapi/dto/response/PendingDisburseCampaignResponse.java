package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.CampaignState;

import java.time.LocalDateTime;

public record PendingDisburseCampaignResponse(
        Long id,
        String projectName,
        String projectOwnerName,
        String projectOwnerPictureUrl,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        CampaignState state,
        Boolean isDisbursed,
        Double amountToDisburse
) {
}
