package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestResponse;
import com.mohdiop.m3fundapi.entity.Action;
import com.mohdiop.m3fundapi.entity.ValidationRequest;
import com.mohdiop.m3fundapi.entity.enums.ActionType;
import com.mohdiop.m3fundapi.entity.enums.EntityName;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import com.mohdiop.m3fundapi.entity.enums.ValidationState;
import com.mohdiop.m3fundapi.repository.ActionRepository;
import com.mohdiop.m3fundapi.repository.AdministratorRepository;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.ValidationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
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
    private final AdministratorRepository administratorRepository;
    private final ActionRepository actionRepository;

    private final EmailService emailService;

    public ValidationRequestService(ValidationRequestRepository validationRequestRepository, ProjectOwnerRepository projectOwnerRepository, AdministratorRepository administratorRepository, ActionRepository actionRepository, EmailService emailService) {
        this.validationRequestRepository = validationRequestRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.administratorRepository = administratorRepository;
        this.actionRepository = actionRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ValidationRequestResponse validateOwner(
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
        if(owner.getState() == UserState.ACTIVE || validationRequest.getState() == ValidationState.APPROVED) {
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
        sendValidationStateEmail(owner.getEmail(), ValidationState.APPROVED);
        validationRequest.setState(ValidationState.APPROVED);
        return validationRequestRepository.save(validationRequest).toResponse();
    }

    public ValidationRequestResponse refuseValidation(
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
        if(owner.getState() == UserState.ACTIVE || validationRequest.getState() == ValidationState.APPROVED) {
            throw new BadRequestException("Utilisateur déjà validé.");
        }
        if(validationRequest.getState() == ValidationState.REFUSED) {
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
        sendValidationStateEmail(owner.getEmail(), ValidationState.REFUSED);
        validationRequest.setState(ValidationState.REFUSED);
        return validationRequestRepository.save(validationRequest).toResponse();
    }

    public void sendValidationStateEmail(
            String recipientEmail,
            ValidationState state
    ) {
        String subject = "Validation de compte";
        String content = "";
        switch (state) {
            case APPROVED -> {
                content = """
                        Bonjour, nous vous informons que votre compte a bien été validé.
                        Vous pouvez vous connecter sur https://www.example.com.
                        L'équipe de M3Fund.
                        """;
            }
            case REFUSED -> {
                content = """
                        Bonjour, nous regrettons de vous annoncer que nous n'avions pas pu procéder à la validation de votre compte.
                        L'équipe de M3Fund.
                        """;
            }
        }
        emailService.sendEmail(
                recipientEmail,
                subject,
                content
        );
    }

    public List<ValidationRequestResponse> getAllPendingValidations() {
        var allValidations = validationRequestRepository.findAll();
        if(allValidations.isEmpty()) return new ArrayList<>();
        return allValidations.stream()
                .filter(validationRequest -> validationRequest.getState() == ValidationState.PENDING)
                .map(ValidationRequest::toResponse).toList();
    }

}
