package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.GiftResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gifts")
@Builder
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "contributor_id")
    private Contributor contributor;

    public GiftResponse toResponse() {
        return new GiftResponse(
                id,
                date,
                payment.toResponse(),
                campaign.getId()
        );
    }
}
