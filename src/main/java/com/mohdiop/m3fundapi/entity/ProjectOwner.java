package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.OrganizationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.SimpleOwnerResponse;
import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_owners")
@SuperBuilder
public class ProjectOwner extends User {

    private String firstName;

    private String lastName;

    private String entityName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectOwnerType type;

    private String description;

    @Column(nullable = false)
    private String address;

    private double shareCapital;

    @Column(nullable = false)
    private double annualIncome;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_picture_id")
    private File profilePicture;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "residence_certificate_id")
    private File residenceCertificate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "biometric_card_id")
    private File biometricCard;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "association_status_id")
    private File associationStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rccm_id")
    private File rccm;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_statement_id")
    private File bankStatement;

    @OneToMany(mappedBy = "projectOwner", cascade = CascadeType.ALL)
    private Set<Campaign> campaigns;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "projectOwner")
    private Set<Discussion> discussions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "owner")
    private Set<Project> projects;

    public IndividualProjectOwnerResponse toIndividualResponse() {
        return new IndividualProjectOwnerResponse(
                getId(),
                firstName,
                lastName,
                getEmail(),
                getPhone(),
                address,
                annualIncome,
                (profilePicture != null) ? profilePicture.getUrl() : null,
                biometricCard.getUrl(),
                residenceCertificate.getUrl(),
                (bankStatement != null) ? bankStatement.getUrl() : null,
                type,
                getState(),
                getUserCreatedAt(),
                getUserRoles()
        );
    }

    public AssociationProjectOwnerResponse toAssociationResponse() {
        return new AssociationProjectOwnerResponse(
                getId(),
                entityName,
                getEmail(),
                getPhone(),
                address,
                annualIncome,
                shareCapital,
                (profilePicture != null) ? profilePicture.getUrl() : null,
                associationStatus.getUrl(),
                bankStatement.getUrl(),
                type,
                getState(),
                getUserCreatedAt(),
                getUserRoles()
        );
    }

    public OrganizationProjectOwnerResponse toOrganizationResponse() {
        return new OrganizationProjectOwnerResponse(
                getId(),
                entityName,
                getEmail(),
                getPhone(),
                address,
                annualIncome,
                shareCapital,
                (profilePicture != null) ? profilePicture.getUrl() : null,
                rccm.getUrl(),
                bankStatement.getUrl(),
                type,
                getState(),
                getUserCreatedAt(),
                getUserRoles()
        );
    }

    public SimpleOwnerResponse toSimpleOwnerResponse() {
        String name;
        if (type == ProjectOwnerType.INDIVIDUAL) {
            name = firstName + " " + lastName;
        } else {
            name = entityName;
        }
        return new SimpleOwnerResponse(
                getId(),
                name,
                getEmail(),
                getPhone(),
                type,
                (profilePicture != null) ? profilePicture.getUrl() : null
        );
    }
}
