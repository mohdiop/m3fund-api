package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.UserState;

import java.time.LocalDateTime;

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
        UserState state,
        LocalDateTime createdAt
) {
}
