package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;

import java.time.LocalDateTime;
import java.util.Set;

public record SimpleUserResponse(
        Long id,
        UserState state,
        Set<UserRole> roles,
        LocalDateTime createdAt
) {
}
