package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.NotificationResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthenticationService authenticationService;

    public NotificationController(NotificationService notificationService, AuthenticationService authenticationService) {
        this.notificationService = notificationService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authenticationService.getCurrentUserId()
                )
        );
    }
}
