package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateAdministratorRequest;
import com.mohdiop.m3fundapi.dto.response.AdministratorResponse;
import com.mohdiop.m3fundapi.entity.Administrator;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.repository.AdministratorRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final UserRepository userRepository;

    public AdministratorService(AdministratorRepository administratorRepository, UserRepository userRepository) {
        this.administratorRepository = administratorRepository;
        this.userRepository = userRepository;
    }

    public void initializeSuperAdministrator(
            CreateAdministratorRequest createAdministratorRequest
    ) {
        if (!createAdministratorRequest.userRoles().contains(UserRole.ROLE_SUPER_ADMIN)) {
            throw new RuntimeException("Impossible de démarrer le système avec cet utilisateur.");
        }
        List<Administrator> potentialAdmins = administratorRepository.findByEmailOrPhone(
                createAdministratorRequest.email(),
                createAdministratorRequest.phone()
        );
        if (!potentialAdmins.isEmpty()) {
            for (var potentialAdmin : potentialAdmins) {
                if (!potentialAdmin.getUserRoles().contains(UserRole.ROLE_SUPER_ADMIN)) {
                    throw new IllegalArgumentException(
                            "Email ou téléphone non valide pour initialisation."
                    );
                }
            }
            return;
        }
        Administrator superAdministrator = createAdministratorRequest.toAdministrator();
        administratorRepository.save(superAdministrator);
    }

    public AdministratorResponse createAdministrator(
            CreateAdministratorRequest createAdministratorRequest
    ) throws BadRequestException {
        if (!createAdministratorRequest.userRoles().contains(UserRole.ROLE_VALIDATIONS_ADMIN)
                && !createAdministratorRequest.userRoles().contains(UserRole.ROLE_USERS_ADMIN)
                && !createAdministratorRequest.userRoles().contains(UserRole.ROLE_PAYMENTS_ADMIN)) {
            throw new BadRequestException("L'utilisateur doit être un administrateur.");
        }
        if (userRepository.findByEmail(createAdministratorRequest.email()).isPresent()) {
            throw new BadRequestException("Email non valide!");
        }
        if (userRepository.findByPhone(createAdministratorRequest.phone()).isPresent()) {
            throw new BadRequestException("Téléphone invalide!");
        }
        return administratorRepository.save(
                createAdministratorRequest.toAdministrator()
        ).toResponse();
    }
}
