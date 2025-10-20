package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.RewardType;

public record RewardResponse(
        Long id,
        String name,
        String description,
        RewardType type,
        long quantity,
        Double unlockAmount
) {
}
