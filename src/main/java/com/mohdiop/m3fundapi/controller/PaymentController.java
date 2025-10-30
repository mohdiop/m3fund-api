package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.response.PaymentResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contributors/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthenticationService authenticationService;

    public PaymentController(PaymentService paymentService, AuthenticationService authenticationService) {
        this.paymentService = paymentService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('CONTRIBUTOR')")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllContributorSPayments() {
        return ResponseEntity.ok(
                paymentService.getAllContributorPayments(
                        authenticationService.getCurrentUserId()
                )
        );
    }
}
