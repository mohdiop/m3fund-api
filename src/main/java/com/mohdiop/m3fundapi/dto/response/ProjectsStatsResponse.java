package com.mohdiop.m3fundapi.dto.response;

public record ProjectsStatsResponse(
        long totalProjects,
        long validatedProjects,
        long pendingProjects,
        long projectsWithActiveCampaigns
) {
}
