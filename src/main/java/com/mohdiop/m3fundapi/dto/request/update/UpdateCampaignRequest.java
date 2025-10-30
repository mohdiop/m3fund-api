package com.mohdiop.m3fundapi.dto.request.update;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignRequest {

    private String title;

    private String description;

    @Positive(message = "Le budget cible doit être positif")
    private Double targetBudget;

    @Positive(message = "Le pourcentage de part offerte doit être positif")
    private Double shareOffered;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @Positive(message = "Le nombre de bénévoles cible doit être positif")
    private Integer targetVolunteer;
}

