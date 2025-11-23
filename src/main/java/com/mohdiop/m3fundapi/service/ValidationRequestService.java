package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.ValidationRequestProjectResponse;
import com.mohdiop.m3fundapi.entity.Action;
import com.mohdiop.m3fundapi.entity.ValidationRequest;
import com.mohdiop.m3fundapi.entity.enums.ActionType;
import com.mohdiop.m3fundapi.entity.enums.EntityName;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import com.mohdiop.m3fundapi.entity.enums.ValidationState;
import com.mohdiop.m3fundapi.repository.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationRequestService {
    private final ValidationRequestRepository validationRequestRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final ProjectRepository projectRepository;
    private final AdministratorRepository administratorRepository;
    private final ActionRepository actionRepository;

    private final EmailService emailService;

    public ValidationRequestService(ValidationRequestRepository validationRequestRepository, ProjectOwnerRepository projectOwnerRepository, ProjectRepository projectRepository, AdministratorRepository administratorRepository, ActionRepository actionRepository, EmailService emailService) {
        this.validationRequestRepository = validationRequestRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.projectRepository = projectRepository;
        this.administratorRepository = administratorRepository;
        this.actionRepository = actionRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ValidationRequestOwnerResponse validateOwner(
            Long authorId,
            Long ownerId
    ) throws BadRequestException {
        var owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var validationRequest = validationRequestRepository.findByOwnerId(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Pas de demande de validation pour cet utilisateur.")
                );
        var author = administratorRepository.findById(authorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (owner.getState() == UserState.ACTIVE && validationRequest.getState() == ValidationState.APPROVED) {
            throw new BadRequestException("Utilisateur déjà validé.");
        }

        owner.setState(UserState.ACTIVE);
        projectOwnerRepository.save(owner);
        actionRepository.save(
                Action.builder()
                        .id(null)
                        .actionType(ActionType.VALIDATION)
                        .entityName(EntityName.USER)
                        .actionDate(LocalDateTime.now())
                        .user(owner)
                        .author(author)
                        .build()
        );
        sendValidationStateEmail(owner.getEmail(), ValidationState.APPROVED, validationRequest.getEntity(), null);
        validationRequest.setState(ValidationState.APPROVED);
        return validationRequestRepository.save(validationRequest).toOwnerResponse();
    }

    @Transactional
    public ValidationRequestOwnerResponse refuseOwner(
            Long authorId,
            Long ownerId
    ) throws BadRequestException {
        var owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var validationRequest = validationRequestRepository.findByOwnerId(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Pas de demande de validation pour cet utilisateur.")
                );
        var author = administratorRepository.findById(authorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (owner.getState() == UserState.ACTIVE && validationRequest.getState() == ValidationState.APPROVED) {
            throw new BadRequestException("Utilisateur déjà validé.");
        }
        if (validationRequest.getState() == ValidationState.REFUSED && owner.getState() == UserState.INACTIVE) {
            throw new BadRequestException("Demande déjà réfusée.");
        }
        owner.setState(UserState.INACTIVE);
        projectOwnerRepository.save(owner);
        actionRepository.save(
                Action.builder()
                        .id(null)
                        .actionType(ActionType.REFUSAL)
                        .entityName(EntityName.USER)
                        .actionDate(LocalDateTime.now())
                        .user(owner)
                        .author(author)
                        .build()
        );
        sendValidationStateEmail(owner.getEmail(), ValidationState.REFUSED, validationRequest.getEntity(), null);
        validationRequest.setState(ValidationState.REFUSED);
        return validationRequestRepository.save(validationRequest).toOwnerResponse();
    }

    public ValidationRequestProjectResponse validateProject(
            Long authorId,
            Long projectId
    ) throws BadRequestException {
        var project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable.")
                );
        var validation = validationRequestRepository.findByProjectId(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Demande de validation introuvable.")
                );
        var author = administratorRepository.findById(authorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (validation.getState() == ValidationState.APPROVED && project.isValidated()) {
            throw new BadRequestException("Projet déjà validé");
        }
        project.setValidated(true);
        projectRepository.save(project);
        actionRepository.save(
                Action.builder()
                        .id(null)
                        .actionType(ActionType.VALIDATION)
                        .entityName(EntityName.PROJECT)
                        .actionDate(LocalDateTime.now())
                        .project(project)
                        .author(author)
                        .build()
        );
        sendValidationStateEmail(project.getOwner().getEmail(), ValidationState.APPROVED, validation.getEntity(), project.getName());
        validation.setState(ValidationState.APPROVED);
        return validationRequestRepository.save(validation).toProjectResponse();
    }

    public ValidationRequestProjectResponse refuseProject(
            Long authorId,
            Long projectId
    ) throws BadRequestException {
        var project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable.")
                );
        var validation = validationRequestRepository.findByProjectId(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Demande de validation introuvable.")
                );
        var author = administratorRepository.findById(authorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (validation.getState() == ValidationState.APPROVED && project.isValidated()) {
            throw new BadRequestException("Projet déjà validé.");
        }
        if(validation.getState() == ValidationState.REFUSED && !project.isValidated()) {
            throw new BadRequestException("Projet déjà réfusé.");
        }
        project.setValidated(false);
        projectRepository.save(project);
        actionRepository.save(
                Action.builder()
                        .id(null)
                        .actionType(ActionType.REFUSAL)
                        .entityName(EntityName.PROJECT)
                        .actionDate(LocalDateTime.now())
                        .project(project)
                        .author(author)
                        .build()
        );
        sendValidationStateEmail(project.getOwner().getEmail(), ValidationState.REFUSED, validation.getEntity(), project.getName());
        validation.setState(ValidationState.REFUSED);
        return validationRequestRepository.save(validation).toProjectResponse();
    }

    public void sendValidationStateEmail(
            String recipientEmail,
            ValidationState state,
            EntityName entityName,
            @Nullable String projectName
    ) {
        String subject = "No title";
        switch (entityName) {
            case PROJECT -> subject = "Validation de projet";
            case USER -> subject = "Validation de compte";
        }
        String content = "";
        switch (state) {
            case APPROVED -> {
                switch (entityName) {
                    case PROJECT -> content = String.format("""
                            Bonjour, nous vous informons que votre projet %s a bien été validé.
                            Vous pouvez maintenant lancer une campagne de financement pour ce projet.
                            L'équipe de M3Fund.
                            """, projectName);
                    case USER -> content = """
                            Bonjour, nous vous informons que votre compte a bien été validé.
                            Vous pouvez vous connecter sur https://www.example.com.
                            L'équipe de M3Fund.
                            """;
                }
            }
            case REFUSED -> {
                switch (entityName) {
                    case PROJECT -> content = String.format("""
                            Bonjour, nous regrettons de vous annoncer que nous n'avions pas pu procéder à la validation de votre projet %s.
                            L'équipe de M3Fund.
                            """, projectName);
                    case USER -> content = """
                            Bonjour, nous regrettons de vous annoncer que nous n'avions pas pu procéder à la validation de votre compte.
                            L'équipe de M3Fund.
                            """;
                }
            }
        }
        emailService.sendEmail(
                recipientEmail,
                subject,
                content
        );
    }

    public List<ValidationRequestOwnerResponse> getAllPendingOwnersValidations() {
        var allValidations = validationRequestRepository.findAll();
        if (allValidations.isEmpty()) return new ArrayList<>();
        return allValidations.stream()
                .filter(validationRequest -> validationRequest.getState() == ValidationState.PENDING)
                .filter(validationRequest -> validationRequest.getEntity() == EntityName.USER)
                .map(ValidationRequest::toOwnerResponse).toList();
    }

    public List<ValidationRequestProjectResponse> getAllPendingProjectsValidations() {
        var allValidations = validationRequestRepository.findAll();
        if (allValidations.isEmpty()) return new ArrayList<>();
        return allValidations.stream()
                .filter(validationRequest -> validationRequest.getState() == ValidationState.PENDING)
                .filter(validationRequest -> validationRequest.getEntity() == EntityName.PROJECT)
                .map(ValidationRequest::toProjectResponse).toList();
    }

}
