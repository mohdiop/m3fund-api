package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Localization;

public record CreateLocalizationRequest(

        String town,
        String region,
        String street,
        double longitude,
        double latitude
) {
    public Localization toLocalization() {
        return Localization.builder()
                .id(null)
                .town(town)
                .region(region)
                .street(street)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
