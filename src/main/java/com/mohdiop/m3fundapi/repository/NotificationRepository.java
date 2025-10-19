package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
