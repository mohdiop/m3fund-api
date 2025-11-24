package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record SystemResponse(
        String name,
        String version,
        Double fund,
        LocalDateTime createdAt
) {
}
