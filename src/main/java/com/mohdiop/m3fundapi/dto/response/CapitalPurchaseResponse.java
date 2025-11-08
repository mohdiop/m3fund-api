package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record CapitalPurchaseResponse(
        Long id,
        LocalDateTime date,
        double shareAcquired,
        Long campaignId,
        Long contributorId,
        boolean isValidatedByContributor,
        boolean isValidatedByOwner,
        PaymentResponse payment
) {
}
