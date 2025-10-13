package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String resume;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectDomain domain;

    @Column(nullable = false)
    private String objective;

    @Column(nullable = false)
    private String websiteLink;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "project_images",
            joinColumns = @JoinColumn(name = "project_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "file_id", nullable = false)
    )
    private Set<File> images;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id")
    private File video;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "business_plan_id")
    private File businessPlan;

    @Column(nullable = false)
    private boolean isValidated;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reward> rewards;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Campaign> campaigns;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments;
}

