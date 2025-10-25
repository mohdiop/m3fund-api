package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.VolunteerResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "volunteers")
@Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    private Contributor contributor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    public VolunteerResponse toResponse() {
        return new VolunteerResponse(
                id,
                contributor.getId(),
                campaign.getId(),
                date
        );
    }
}
