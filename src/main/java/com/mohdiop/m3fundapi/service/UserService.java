package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.CheckForEmailAndPhoneValidityRequest;
import com.mohdiop.m3fundapi.entity.Administrator;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.User;
import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Record me(
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (user instanceof Administrator) {
            return ((Administrator) user).toResponse();
        }
        if (user instanceof ProjectOwner) {
            if (((ProjectOwner) user).getType() == ProjectOwnerType.INDIVIDUAL) {
                return ((ProjectOwner) user).toIndividualResponse();
            }
            if (((ProjectOwner) user).getType() == ProjectOwnerType.ASSOCIATION) {
                return ((ProjectOwner) user).toAssociationResponse();
            }
            return ((ProjectOwner) user).toOrganizationResponse();
        }
        if (user instanceof Contributor) {
            return ((Contributor) user).toResponse();
        }
        return null;
    }

    public void checkForEmailAndPhoneValidity(
            CheckForEmailAndPhoneValidityRequest checkRequest
    ) throws BadRequestException {
        if(checkRequest.email() != null) {
            if (userRepository.findByEmail(checkRequest.email()).isPresent()) {
                throw new BadRequestException("Email indisponible, choisissez un autre.");
            }
        }
        if (userRepository.findByPhone(checkRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone indisponible, choisissez un autre.");
        }
    }
}
