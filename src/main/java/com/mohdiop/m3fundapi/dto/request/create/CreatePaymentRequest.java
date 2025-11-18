package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Payment;
import com.mohdiop.m3fundapi.entity.enums.PaymentState;
import com.mohdiop.m3fundapi.entity.enums.PaymentStrategy;
import com.mohdiop.m3fundapi.entity.enums.PaymentType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreatePaymentRequest(

        @NotBlank(message = "L'identifiant de la transaction est obligatoire.")
        @Size(min = 5, max = 100, message = "L'identifiant de la transaction doit comporter entre 5 et 100 caractères.")
        String transactionId,

        @NotNull(message = "Le type de paiement est obligatoire.")
        PaymentType type,

        @NotNull(message = "L'état du paiement est obligatoire.")
        PaymentState state,

        @Positive(message = "Le montant du paiement doit être strictement supérieur à 0.")
        @Digits(integer = 12, fraction = 2, message = "Le montant doit être un nombre valide avec au maximum 2 décimales.")
        double amount

) {

    public Payment toPayment() {
        return Payment.builder()
                .id(null)
                .transactionId(transactionId)
                .type(type)
                .state(state)
                .carriedOutOn(LocalDateTime.now())
                .amount(amount)
                .strategy(PaymentStrategy.CASHED)
                .build();
    }
}
