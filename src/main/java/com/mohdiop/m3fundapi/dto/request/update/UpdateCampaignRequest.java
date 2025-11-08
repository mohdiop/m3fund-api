package com.mohdiop.m3fundapi.dto.request.update;

import com.mohdiop.m3fundapi.dto.request.create.CreateRewardRequest;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateCampaignRequest(
        @Future(message = "La date de fin doit être ultérieure à la date actuelle.")
        LocalDateTime endAt,

        CampaignType type,

        @Positive(message = "Le nombre de volontaires cibles doit être positif.")
        Long targetVolunteer,

        @Positive(message = "Le budget cible doit être positif.")
        Double targetBudget,

        @Positive(message = "La part offerte doit être positive.")
        Double shareOffered,

        @Valid
        Set<CreateRewardRequest> rewards
) {
}
