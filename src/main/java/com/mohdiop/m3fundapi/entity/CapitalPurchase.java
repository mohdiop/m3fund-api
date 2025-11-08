package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.CapitalPurchaseResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capital_purchases")
@Builder
public class CapitalPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "share_acquired", columnDefinition = "DOUBLE CHECK (share_acquired >= 0 AND share_acquired <= 100)")
    private double shareAcquired;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "contributor_id")
    private Contributor contributor;

    @Column(nullable = false, columnDefinition = "BOOL DEFAULT FALSE")
    private boolean isValidatedByInvestor;

    @Column(nullable = false, columnDefinition = "BOOL DEFAULT FALSE")
    private boolean isValidatedByProjectOwner;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;

    public CapitalPurchaseResponse toResponse() {
        return new CapitalPurchaseResponse(
                id,
                date,
                shareAcquired,
                campaign.getId(),
                contributor.getId(),
                isValidatedByInvestor,
                isValidatedByProjectOwner,
                payment.toResponse()
        );
    }
}
