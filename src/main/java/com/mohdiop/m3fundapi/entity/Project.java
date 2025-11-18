package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.OwnerProjectResponse;
import com.mohdiop.m3fundapi.dto.response.ProjectResponse;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
@Builder
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

    @Column(nullable = false)
    private LocalDateTime launchedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL)
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
    private Set<Campaign> campaigns;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private ProjectOwner owner;

    public ProjectResponse toResponse() {
        var imagesUrl = new HashSet<String>();
        for (var image : images) {
            imagesUrl.add(image.getUrl());
        }
        return new ProjectResponse(
                id,
                name,
                description,
                resume,
                objective,
                domain,
                websiteLink,
                imagesUrl,
                video.getUrl(),
                businessPlan.getUrl(),
                launchedAt,
                createdAt,
                isValidated
        );
    }

    public OwnerProjectResponse toOwnerProjectResponse() {
        var imagesUrl = new HashSet<String>();
        for (var image : images) {
            imagesUrl.add(image.getUrl());
        }
        return new OwnerProjectResponse(
                id,
                name,
                description,
                resume,
                objective,
                domain,
                websiteLink,
                imagesUrl,
                video.getUrl(),
                businessPlan.getUrl(),
                launchedAt,
                createdAt,
                isValidated,
                owner.toSimpleOwnerResponse()
        );
    }
}

