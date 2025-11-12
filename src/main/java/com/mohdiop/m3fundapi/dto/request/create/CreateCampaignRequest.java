package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.annotation.ValidCampaign;
import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

@ValidCampaign
public record CreateCampaignRequest(

        @NotNull(message = "La date de fin de campagne est obligatoire.")
        @Future(message = "La date de fin doit être ultérieure à la date actuelle.")
        LocalDateTime endAt,

        @NotNull(message = "Le type de campagne est obligatoire.")
        CampaignType type,

        Long targetVolunteer,

        Double targetBudget,

        Double shareOffered,

        @Valid
        Set<CreateRewardRequest> rewards

) {

    public Campaign toDonationCampaign() {
        return Campaign.builder()
                .id(null)
                .launchedAt(LocalDateTime.now())
                .endAt(endAt)
                .targetBudget(targetBudget)
                .type(CampaignType.DONATION)
                .state(CampaignState.PENDING)
                .build();
    }

    public Campaign toVolunteeringCampaign() {
        return Campaign.builder()
                .id(null)
                .launchedAt(LocalDateTime.now())
                .endAt(endAt)
                .targetVolunteer(targetVolunteer)
                .type(CampaignType.VOLUNTEERING)
                .state(CampaignState.PENDING)
                .build();
    }

    public Campaign toInvestmentCampaign() {
        return Campaign.builder()
                .id(null)
                .launchedAt(LocalDateTime.now())
                .endAt(endAt)
                .shareOffered(shareOffered)
                .type(CampaignType.INVESTMENT)
                .state(CampaignState.PENDING)
                .build();
    }
}
