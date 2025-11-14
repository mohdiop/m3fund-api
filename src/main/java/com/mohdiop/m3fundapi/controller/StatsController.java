package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.AdminDashboardResponse;
import com.mohdiop.m3fundapi.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN', 'SYSTEM', 'VALIDATIONS_ADMIN', 'USERS_ADMIN', 'PAYMENTS_ADMIN')"
    )
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboardStats() {
        return ResponseEntity.ok(
                statsService.getDashboardStats()
        );
    }
}
