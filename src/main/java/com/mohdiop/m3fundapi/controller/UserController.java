package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.ChangePasswordRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProfileRequest;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/me")
    public ResponseEntity<Record> me() {
        return ResponseEntity.ok(
                userService.me(
                        authenticationService.getCurrentUserId()
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<Record> updateProfile(@Valid @ModelAttribute UpdateProfileRequest request) {
        return ResponseEntity.ok(
                userService.updateProfile(
                        authenticationService.getCurrentUserId(),
                        request
                )
        );
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(
                userService.changePassword(
                        authenticationService.getCurrentUserId(),
                        request
                )
        );
    }
}
