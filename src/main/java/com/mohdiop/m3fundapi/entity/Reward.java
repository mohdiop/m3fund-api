package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.RewardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rewards")
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
    private int quantity;

    @Column(nullable = false)
    private Double unlockAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;
}
