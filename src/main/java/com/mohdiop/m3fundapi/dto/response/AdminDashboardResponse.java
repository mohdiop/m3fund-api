package com.mohdiop.m3fundapi.dto.response;

import java.util.List;

public record AdminDashboardResponse(
        Long totalActiveUsers,
        Long monthlyNewUsers,
        Double usersCurrentMonthScore,
        Double usersLastMonthScore,
        Long totalActiveProjects,
        Long monthlyNewProjects,
        Double projectsCurrentMonthScore,
        Double projectsLastMonthScore,
        Double actualFund,
        Double totalFund,
        List<PaymentResponse> payments,
        List<ProjectResponse> projects
) {
}
