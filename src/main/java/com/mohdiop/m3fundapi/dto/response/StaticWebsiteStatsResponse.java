package com.mohdiop.m3fundapi.dto.response;

public record StaticWebsiteStatsResponse(
        Long totalUsers,
        Long totalProjects,
        Double totalPaymentAmount
) {
}
