package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.RewardWinningResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.RewardWinningService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contributors")
public class RewardController {

    private final RewardWinningService rewardWinningService;
    private final AuthenticationService authenticationService;

    public RewardController(RewardWinningService rewardWinningService, AuthenticationService authenticationService) {
        this.rewardWinningService = rewardWinningService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @GetMapping("/rewards-won")
    public ResponseEntity<List<RewardWinningResponse>> getMyRewards() {
        return ResponseEntity.ok(rewardWinningService.getMyRewards(
                authenticationService.getCurrentUserId()
        ));
    }
}
