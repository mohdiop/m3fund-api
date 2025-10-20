package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;

import java.time.LocalDateTime;
import java.util.Set;

public record OwnerProjectResponse(
        Long id,
        String name,
        String description,
        String resume,
        String objective,
        ProjectDomain domain,
        String websiteLink,
        Set<String> imagesUrl,
        String videoUrl,
        String businessPlanUrl,
        LocalDateTime launchedAt,
        LocalDateTime createdAt,
        Boolean isValidated,
        SimpleOwnerResponse owner
) {
}
