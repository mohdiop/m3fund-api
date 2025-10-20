package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contributors")
@SuperBuilder
public class Contributor extends User {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "localization_id", nullable = false)
    private Localization localization;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "contributor_project_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "domain_preference", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProjectDomain> projectDomains;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "contributor_campaign_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "campaign_preference", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<CampaignType> campaignTypes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "contributor")
    private Set<Discussion> discussions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "contributor")
    private Set<Gift> gifts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "contributor")
    private Set<RewardWinning> rewardWinnings;

    public ContributorResponse toResponse() {
        return new ContributorResponse(getId(), firstName, lastName, getEmail(), getPhone(), localization.toResponse(), projectDomains, campaignTypes, getState(), getUserCreatedAt());
    }
}
