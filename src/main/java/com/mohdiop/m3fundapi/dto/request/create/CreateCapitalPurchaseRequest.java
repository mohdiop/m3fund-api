package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.CapitalPurchase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateCapitalPurchaseRequest(
        @NotNull(message = "La part acquise est obligatoire.")
        @DecimalMin(value = "0.0", inclusive = false, message = "La part doit être supérieure à 0.0.")
        @DecimalMax(value = "100.0", message = "La part doit pas dépassée 100.0")
        double shareAcquired,
        @Valid CreatePaymentRequest payment
) {
    public CapitalPurchase toCapitalPurchase() {
        return CapitalPurchase.builder()
                .id(null)
                .shareAcquired(shareAcquired)
                .payment(payment.toPayment())
                .date(LocalDateTime.now())
                .isValidatedByInvestor(true)
                .isValidatedByProjectOwner(true)
                .build();
    }
}
