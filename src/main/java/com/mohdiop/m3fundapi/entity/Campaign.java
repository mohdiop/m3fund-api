package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "project_owner_id")
    private ProjectOwner projectOwner;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "campaign")
    @Transient
    private CapitalPurchase capitalPurchase;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "campaign")
    @Transient
    private Gift gift;

    @Column(nullable = false)
    private LocalDateTime launchedAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private double targetBudget;

    @Column(columnDefinition = "DOUBLE CHECK (shareOffered >= 0 AND shareOffered <= 100)")
    private double shareOffered;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignState state;
}
