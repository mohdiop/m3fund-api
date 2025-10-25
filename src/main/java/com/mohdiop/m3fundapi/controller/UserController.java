package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.CheckForEmailAndPhoneValidityRequest;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
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
}
