package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.NotificationResponse;
import com.mohdiop.m3fundapi.entity.Notification;
import com.mohdiop.m3fundapi.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponse> getMyNotifications(
            Long userId
    ) {
        var notifications = notificationRepository.findByToUserId(userId);
        if(notifications.isEmpty()) return new ArrayList<>();
        return notifications.stream().map(Notification::toResponse).toList();
    }
}
