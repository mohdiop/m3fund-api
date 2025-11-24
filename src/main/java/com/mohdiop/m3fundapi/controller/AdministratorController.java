package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateAdministratorRequest;
import com.mohdiop.m3fundapi.dto.response.AdministratorResponse;
import com.mohdiop.m3fundapi.dto.response.SystemResponse;
import com.mohdiop.m3fundapi.service.AdministratorService;
import com.mohdiop.m3fundapi.service.SystemService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
public class AdministratorController {

    private final AdministratorService administratorService;
    private final SystemService systemService;

    public AdministratorController(AdministratorService administratorService, SystemService systemService) {
        this.administratorService = administratorService;
        this.systemService = systemService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<AdministratorResponse> createAdministrator(
            @Valid @RequestBody CreateAdministratorRequest createAdministratorRequest
    ) throws BadRequestException {
        return new ResponseEntity<>(
                administratorService.createAdministrator(
                        createAdministratorRequest
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/system")
    public ResponseEntity<SystemResponse> getSystemInfo() {
        return ResponseEntity.ok(
                systemService.getSystemInfo()
        );
    }
}
