package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.entity.enums.PaymentState;
import com.mohdiop.m3fundapi.entity.enums.PaymentStrategy;
import com.mohdiop.m3fundapi.entity.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Column(nullable = false)
    private LocalDateTime carriedOutOn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false, columnDefinition = "ENUM('CASHED', 'DISBURSED') DEFAULT 'CASHED'")
    @Enumerated(EnumType.STRING)
    private PaymentStrategy strategy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payment")
    private Gift gift;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payment")
    private CapitalPurchase capitalPurchase;

    public PaymentResponse toResponse(String... name) {
        return new PaymentResponse(
                id,
                transactionId,
                type,
                state,
                carriedOutOn,
                amount,
                gift == null ? capitalPurchase == null ? Arrays.stream(name).findFirst().orElse("") : capitalPurchase.getCampaign().getProject().getName() : gift.getCampaign().getProject().getName(),
                strategy
        );
    }
}
