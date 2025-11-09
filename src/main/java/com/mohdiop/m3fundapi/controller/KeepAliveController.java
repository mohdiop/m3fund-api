package com.mohdiop.m3fundapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/keep-alive")
public class KeepAliveController {

    @GetMapping
    public ResponseEntity<String> keepAlive() {
        return ResponseEntity.ok(
                "Keeping alive ..."
        );
    }
}
