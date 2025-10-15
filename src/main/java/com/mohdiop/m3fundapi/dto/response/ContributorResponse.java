package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import com.mohdiop.m3fundapi.entity.enums.UserState;

import java.time.LocalDateTime;
import java.util.Set;

public record ContributorResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalizationResponse localization,
        Set<ProjectDomain> projectDomainPrefs,
        Set<CampaignType> campaignTypePrefs,
        UserState state,
        LocalDateTime createdAt
) {
}
