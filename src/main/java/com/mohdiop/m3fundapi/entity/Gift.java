package com.mohdiop.m3fundapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gifts")
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "payment_id")
    private Payment payment;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<RewardWinning> rewardWinnings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "campaign_id")
    private Campaign campaign;
}
