package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        LocalDateTime date,
        PaymentResponse payment,
        Long campaignId,
        String campaignTitle,
        String projectName,
        String projectDomain,
        String projectDescription
) {
}

