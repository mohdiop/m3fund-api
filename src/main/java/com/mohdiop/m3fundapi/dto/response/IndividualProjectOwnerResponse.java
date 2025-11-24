package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;

import java.time.LocalDateTime;
import java.util.Set;

public record IndividualProjectOwnerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        double annualIncome,
        String profilePictureUrl,
        String biometricCardUrl,
        String residenceCertificateUrl,
        String bankStatementUrl,
        ProjectOwnerType type,
        UserState state,
        LocalDateTime createdAt,
        Set<UserRole> roles,
        Double fund
) {
}
