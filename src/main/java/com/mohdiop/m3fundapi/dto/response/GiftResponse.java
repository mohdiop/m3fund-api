package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record GiftResponse(
        long id,
        LocalDateTime date,
        PaymentResponse payment,
        long campaignId
) {
}
