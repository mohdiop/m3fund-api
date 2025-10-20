package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateGiftRequest;
import com.mohdiop.m3fundapi.dto.response.GiftResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.GiftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/campaigns")
public class GiftController {

    private final AuthenticationService authenticationService;
    private final GiftService giftService;

    public GiftController(AuthenticationService authenticationService, GiftService giftService) {
        this.authenticationService = authenticationService;
        this.giftService = giftService;
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @PostMapping("/{campaignId}/gifts")
    public ResponseEntity<GiftResponse> createGift(
            @PathVariable Long campaignId,
            @Valid @RequestBody CreateGiftRequest createGiftRequest
    ) {
        return new ResponseEntity<>(
                giftService.createGift(
                        authenticationService.getCurrentUserId(),
                        campaignId,
                        createGiftRequest
                ),
                HttpStatus.CREATED
        );
    }
}
