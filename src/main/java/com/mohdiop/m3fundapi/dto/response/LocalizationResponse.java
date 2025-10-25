package com.mohdiop.m3fundapi.dto.response;

public record LocalizationResponse(
        Long id,
        String country,
        String town,
        String region,
        String street,
        double longitude,
        double latitude
) {
}
