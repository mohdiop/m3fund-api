package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.RewardWinningState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reward_winnings")
public class RewardWinning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RewardWinningState state;

    @ManyToOne
    @JoinColumn(name = "gift_id", nullable = false)
    private Gift gift;
}
