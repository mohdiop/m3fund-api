package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record VolunteerResponse(
        Long id,
        Long contributorId,
        Long campaignId,
        LocalDateTime date
) {
}
