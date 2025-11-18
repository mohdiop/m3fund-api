package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.PaymentState;
import com.mohdiop.m3fundapi.entity.enums.PaymentStrategy;
import com.mohdiop.m3fundapi.entity.enums.PaymentType;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String transactionId,
        PaymentType type,
        PaymentState state,
        LocalDateTime madeAt,
        double amount,
        String projectName,
        PaymentStrategy strategy
) {
}
