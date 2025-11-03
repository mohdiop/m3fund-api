package com.mohdiop.m3fundapi.dto.response;

import java.util.Set;

public record ContributionResponse(
        Set<GiftResponse> gifts,
        Set<VolunteerResponse> volunteering
) {
}
