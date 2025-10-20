package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.RewardWinningState;

import java.time.LocalDateTime;

public record RewardWinningResponse(
        Long id,
        LocalDateTime gainedAt,
        RewardWinningState state,
        RewardResponse reward
) {
}
