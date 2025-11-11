package com.mohdiop.m3fundapi.dto.response;

import com.mohdiop.m3fundapi.entity.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String content,
        String senderName,
        LocalDateTime sentAt,
        boolean isRead,
        NotificationType type
) {
}
