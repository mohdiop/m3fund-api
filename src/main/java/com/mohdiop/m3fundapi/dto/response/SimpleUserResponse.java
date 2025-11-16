package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record SimpleUserResponse(
        Long id,
        LocalDateTime createdAt
) {
}
