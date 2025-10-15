package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateAdministratorRequest;
import com.mohdiop.m3fundapi.entity.Administrator;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.repository.AdministratorRepository;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    public AdministratorService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    public void initializeSuperAdministrator(
            CreateAdministratorRequest createAdministratorRequest
    ) {
        if (!createAdministratorRequest.userRoles().contains(UserRole.SUPER_ADMIN)) {
            throw new RuntimeException("Impossible de démarrer le système avec cet utilisateur.");
        }
        Administrator superAdministrator = createAdministratorRequest.toAdministrator();
        administratorRepository.save(superAdministrator);
    }
}
