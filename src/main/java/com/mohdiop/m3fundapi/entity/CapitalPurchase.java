package com.mohdiop.m3fundapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capital_purchases")
public class CapitalPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "share_acquired", columnDefinition = "DOUBLE CHECK (share_acquired >= 0 AND share_acquired <= 100)")
    private double shareAcquired;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "contributor_id")
    private Contributor contributor;

    @Column(nullable = false, columnDefinition = "BOOL DEFAULT FALSE")
    private boolean isValidatedByInvestor;

    @Column(nullable = false, columnDefinition = "BOOL DEFAULT FALSE")
    private boolean isValidatedByProjectOwner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;
}
