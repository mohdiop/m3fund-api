package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "campaign", cascade = CascadeType.ALL)
    private CapitalPurchase capitalPurchase;

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

    @Column(nullable = false, columnDefinition = "BOOL DEFAULT FALSE")
    private boolean isDisbursed;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reward> rewards;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Gift> gifts;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Volunteer> volunteers;

    public int getCurrentVolunteerNumber() {
        if (type != CampaignType.VOLUNTEERING) {
            return 0;
        }
        if (volunteers == null) {
            return 0;
        }
        return volunteers.size();
    }

    public CampaignResponse toResponse() {
        double currentFund = 0D;
        if (type == CampaignType.DONATION && gifts != null) {
            for (var gift : gifts) {
                currentFund += gift.getPayment().getAmount();
            }
        }
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
                (rewards != null) ? rewards.stream().map(Reward::toResponse).toList() : Collections.emptyList(),
                currentFund,
                getCurrentVolunteerNumber()
        );
    }
}
