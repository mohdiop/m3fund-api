package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.ValidationState;

import java.time.LocalDateTime;

public record ValidationRequestResponse (
        Long id,
        SimpleOwnerResponse owner,
        LocalDateTime date,
        ValidationState state
)
{}
