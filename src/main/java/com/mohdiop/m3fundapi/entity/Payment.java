package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.entity.enums.PaymentState;
import com.mohdiop.m3fundapi.entity.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    public PaymentResponse toResponse() {
        return new PaymentResponse(
                id,
                transactionId,
                type,
                state,
                carriedOutOn,
                amount
        );
    }
}
