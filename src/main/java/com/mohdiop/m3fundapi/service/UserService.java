package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.ChangePasswordRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateProfileRequest;
import com.mohdiop.m3fundapi.entity.Administrator;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.User;
import com.mohdiop.m3fundapi.entity.enums.FileExtension;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UploadService uploadService;

    public UserService(UserRepository userRepository, UploadService uploadService) {
        this.userRepository = userRepository;
        this.uploadService = uploadService;
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
        return null;
    }

    public Record updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        // Vérifier le mot de passe actuel
        if (!BCrypt.checkpw(request.currentPassword(), user.getPassword())) {
            throw new SecurityException("Mot de passe incorrect.");
        }

        if (user instanceof ProjectOwner projectOwner) {
            // Mettre à jour les champs de base
            if (request.firstName() != null && !request.firstName().isEmpty()) {
                projectOwner.setFirstName(request.firstName());
            }
            if (request.lastName() != null && !request.lastName().isEmpty()) {
                projectOwner.setLastName(request.lastName());
            }
            if (request.email() != null && !request.email().isEmpty()) {
                projectOwner.setEmail(request.email());
            }
            if (request.phone() != null && !request.phone().isEmpty()) {
                projectOwner.setPhone(request.phone());
            }
            if (request.address() != null && !request.address().isEmpty()) {
                projectOwner.setAddress(request.address());
            }

            // Mettre à jour la photo de profil si fournie
            if (request.profilePhoto() != null && !request.profilePhoto().isEmpty()) {
                com.mohdiop.m3fundapi.entity.File uploadedFile = uploadService.uploadFile(
                        request.profilePhoto(),
                        UUID.randomUUID().toString(),
                        FileType.PICTURE,
                        uploadService.getFileExtension(request.profilePhoto())
                );
                projectOwner.setProfilePicture(uploadedFile);
            }

            ProjectOwner updatedUser = userRepository.save(projectOwner);

            // Retourner la réponse appropriée selon le type
            if (updatedUser.getType() == ProjectOwnerType.INDIVIDUAL) {
                return updatedUser.toIndividualResponse();
            } else if (updatedUser.getType() == ProjectOwnerType.ASSOCIATION) {
                return updatedUser.toAssociationResponse();
            } else {
                return updatedUser.toOrganizationResponse();
            }
        }

        throw new RuntimeException("Type d'utilisateur non supporté pour la mise à jour.");
    }

    public Map<String, String> changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        // Vérifier que le nouveau mot de passe et la confirmation correspondent
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe et la confirmation ne correspondent pas.");
        }

        // Vérifier le mot de passe actuel
        if (!BCrypt.checkpw(request.currentPassword(), user.getPassword())) {
            throw new SecurityException("Le mot de passe actuel est incorrect.");
        }

        // Hash et sauvegarder le nouveau mot de passe
        String hashedPassword = BCrypt.hashpw(request.newPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe changé avec succès.");
        return response;
    }
}
