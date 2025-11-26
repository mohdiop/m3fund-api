package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ValidationState;
import com.mohdiop.m3fundapi.entity.enums.ValidationType;

import java.time.LocalDateTime;

public record ValidationRequestOwnerResponse(
        Long id,
        SimpleOwnerResponse owner,
        LocalDateTime date,
        ValidationState state,
        ValidationType type
) {
}
