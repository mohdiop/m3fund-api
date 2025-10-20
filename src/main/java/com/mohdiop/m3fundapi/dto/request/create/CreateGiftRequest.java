package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Gift;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

public record CreateGiftRequest(
        @Valid
        CreatePaymentRequest payment
) {

    public Gift toGift() {
        return Gift.builder()
                .id(null)
                .date(LocalDateTime.now())
                .payment(payment.toPayment())
                .build();
    }
}
