package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.entity.enums.UserState;

import java.time.LocalDateTime;

public record OrganizationProjectOwnerResponse(
        Long id,
        String entityName,
        String email,
        String phone,
        String address,
        double annualIncome,
        String logoUrl,
        String rccmUrl,
        String bankStatementUrl,
        ProjectOwnerType type,
        UserState state,
        LocalDateTime createdAt
) {
}
