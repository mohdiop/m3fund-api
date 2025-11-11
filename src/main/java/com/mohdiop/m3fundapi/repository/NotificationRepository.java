package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByToUserId(Long toUserId);
}
