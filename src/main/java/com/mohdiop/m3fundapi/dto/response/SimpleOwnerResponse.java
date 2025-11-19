package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;

public record SimpleOwnerResponse(
        Long id,
        String name,
        String email,
        String phone,
        ProjectOwnerType type,
        String profileUrl
) {
}
