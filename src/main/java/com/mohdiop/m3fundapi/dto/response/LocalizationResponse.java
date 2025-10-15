package com.mohdiop.m3fundapi.dto.response;

public record LocalizationResponse(
        Long id,
        String town,
        String region,
        String street,
        double longitude,
        double latitude
) {
}
