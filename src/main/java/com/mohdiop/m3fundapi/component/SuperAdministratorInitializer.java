package com.mohdiop.m3fundapi.component;

import com.mohdiop.m3fundapi.dto.request.create.CreateAdministratorRequest;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.service.AdministratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class SuperAdministratorInitializer implements CommandLineRunner {

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

    public SuperAdministratorInitializer(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @Override
    public void run(String... args) throws Exception {
        administratorService.initializeSuperAdministrator(new CreateAdministratorRequest(adminFirstName, adminLastName, adminEmail, adminPhone, adminPassword, new HashSet<>(List.of(UserRole.SUPER_ADMIN))));
    }
}
