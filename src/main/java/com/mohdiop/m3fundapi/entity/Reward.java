package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.RewardResponse;
import com.mohdiop.m3fundapi.entity.enums.RewardType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rewards")
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RewardType type;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private Double unlockAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    public RewardResponse toResponse() {
        return new RewardResponse(
                id,
                name,
                description,
                type,
                quantity,
                unlockAmount
        );
    }
}
