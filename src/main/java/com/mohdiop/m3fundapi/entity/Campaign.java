package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaigns")
@Builder
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
    private CapitalPurchase capitalPurchase;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "campaign")
    private Gift gift;

    @Column(nullable = false)
    private LocalDateTime launchedAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    private double targetBudget;

    private long targetVolunteer;

    @Column(name = "share_offered", columnDefinition = "DOUBLE CHECK (share_offered >= 0 AND share_offered <= 100)")
    private double shareOffered;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignState state;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reward> rewards;

    public CampaignResponse toResponse() {
        return new CampaignResponse(
                id,
                project.toResponse(),
                projectOwner.toSimpleOwnerResponse(),
                launchedAt,
                endAt,
                targetBudget,
                targetVolunteer,
                shareOffered,
                type,
                state,
                rewards.stream().map(Reward::toResponse).toList()
        );
    }
}
