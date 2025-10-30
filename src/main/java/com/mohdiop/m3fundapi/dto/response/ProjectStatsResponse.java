package com.mohdiop.m3fundapi.dto.response;

public record ProjectStatsResponse(
        long totalProjects,
        long validatedProjects,
        long pendingProjects,
        long projectsWithActiveCampaigns
) {
}

