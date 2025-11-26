package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ValidationState;
import com.mohdiop.m3fundapi.entity.enums.ValidationType;

import java.time.LocalDateTime;

public record ValidationRequestProjectResponse(
        Long id,
        ProjectResponse project,
        LocalDateTime date,
        ValidationState state,
        String ownerName,
        ValidationType type
) {
}
