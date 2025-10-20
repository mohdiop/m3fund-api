package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.RewardWinningResponse;
import com.mohdiop.m3fundapi.entity.enums.RewardWinningState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "reward_winnings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"contributor_id", "reward_id"})
        }
)
public class RewardWinning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RewardWinningState state;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "contributor_id")
    private Contributor contributor;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    public RewardWinningResponse toResponse() {
        return new RewardWinningResponse(
                id,
                date,
                state,
                reward.toResponse()
        );
    }
}
