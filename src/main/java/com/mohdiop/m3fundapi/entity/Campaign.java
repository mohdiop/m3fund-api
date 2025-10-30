package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.CampaignDashboardResponse;
import com.mohdiop.m3fundapi.dto.response.CampaignResponse;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Gift> gifts;

    @Column(nullable = false)
    private LocalDateTime launchedAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(columnDefinition = "TEXT")
    private String description;

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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
                rewards != null ? rewards.stream().map(Reward::toResponse).toList() : List.of()
        );
    }

    public CampaignDashboardResponse toDashboardResponse() {
        // Calculer les fonds récoltés
        double fundsRaised = calculateFundsRaised();
        
        // Calculer le nombre de collaborateurs uniques
        int collaboratorCount = calculateCollaboratorCount();
        
        // Calculer la progression
        double progress = targetBudget > 0 ? (fundsRaised / targetBudget) * 100 : 0;
        progress = Math.min(progress, 100); // Cap à 100%
        
        // Calculer la valeur nette (pour les investissements)
        double netValue = type == CampaignType.INVESTMENT ? 
            (targetBudget * shareOffered / 100) : 0;
        
        // Nombre de campagnes du projet
        int campaignCount = project.getCampaigns() != null ? project.getCampaigns().size() : 0;
        
        return new CampaignDashboardResponse(
                id,
                project.getId(),
                project.getName(),
                description != null ? description : project.getDescription(),
                targetBudget,
                shareOffered,
                launchedAt,
                endAt,
                type,
                state,
                progress,
                fundsRaised,
                collaboratorCount,
                campaignCount,
                netValue,
                createdAt,
                updatedAt
        );
    }

    private double calculateFundsRaised() {
        double total = 0.0;
        
        // Ajouter les dons
        if (gifts != null) {
            for (Gift gift : gifts) {
                if (gift.getPayment() != null) {
                    total += gift.getPayment().getAmount();
                }
            }
        }
        
        // Ajouter l'investissement en capital
        if (capitalPurchase != null && capitalPurchase.getPayment() != null) {
            total += capitalPurchase.getPayment().getAmount();
        }
        
        return total;
    }

    private int calculateCollaboratorCount() {
        Set<Long> uniqueContributors = new HashSet<>();
        
        // Compter les contributeurs uniques via les dons
        if (gifts != null) {
            for (Gift gift : gifts) {
                if (gift.getContributor() != null) {
                    uniqueContributors.add(gift.getContributor().getId());
                }
            }
        }
        
        return uniqueContributors.size();
    }
}
