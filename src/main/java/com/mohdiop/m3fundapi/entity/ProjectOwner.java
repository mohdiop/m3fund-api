package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_owners")
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
}
