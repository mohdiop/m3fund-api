package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;

import java.time.LocalDateTime;
import java.util.Set;

public record AssociationProjectOwnerResponse(
        Long id,
        String entityName,
        String email,
        String phone,
        String address,
        Double annualIncome,
        Double shareCapital,
        String logoUrl,
        String associationStatusUrl,
        String bankStatementUrl,
        ProjectOwnerType type,
        UserState state,
        LocalDateTime createdAt,
        Set<UserRole> roles
) {
}
