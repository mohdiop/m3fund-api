package com.mohdiop.m3fundapi.component;

import com.mohdiop.m3fundapi.dto.request.create.CreateAdministratorRequest;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.service.AdministratorService;
import com.mohdiop.m3fundapi.service.SystemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class SuperAdministratorInitializer implements CommandLineRunner {

    private final SystemService systemService;
    private final AdministratorService administratorService;
    @Value("${admin.firstname}")
    private String adminFirstName;
    @Value("${admin.lastname}")
    private String adminLastName;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.phone}")
    private String adminPhone;
    @Value("${app.version}")
    private String appVersion;

    public SuperAdministratorInitializer(SystemService systemService, AdministratorService administratorService) {
        this.systemService = systemService;
        this.administratorService = administratorService;
    }

    @Override
    public void run(String... args) throws Exception {
        systemService.createSystem("M3Fund", appVersion, 0D);
        administratorService.initializeSuperAdministrator(new CreateAdministratorRequest(adminFirstName, adminLastName, adminEmail, adminPhone, adminPassword, new HashSet<>(List.of(UserRole.ROLE_SUPER_ADMIN, UserRole.ROLE_SYSTEM))));
    }
}
