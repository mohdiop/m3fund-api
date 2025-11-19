package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateAssociationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateOrganizationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.OrganizationProjectOwnerResponse;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.ValidationRequest;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import com.mohdiop.m3fundapi.entity.enums.ProjectOwnerType;
import com.mohdiop.m3fundapi.entity.enums.ValidationState;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import com.mohdiop.m3fundapi.repository.ValidationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProjectOwnerService {

    private final ProjectOwnerRepository projectOwnerRepository;
    private final UserRepository userRepository;
    private final ValidationRequestRepository validationRequestRepository;

    private final UploadService uploadService;
    private final EmailService emailService;

    public ProjectOwnerService(ProjectOwnerRepository projectOwnerRepository, UserRepository userRepository, ValidationRequestRepository validationRequestRepository, UploadService uploadService, EmailService emailService) {
        this.projectOwnerRepository = projectOwnerRepository;
        this.userRepository = userRepository;
        this.validationRequestRepository = validationRequestRepository;
        this.uploadService = uploadService;
        this.emailService = emailService;
    }

    @Transactional
    public IndividualProjectOwnerResponse createIndividualProjectOwner(
            CreateIndividualProjectOwnerRequest createIndividualProjectOwnerRequest
    ) throws BadRequestException {
        if (userRepository.findByEmail(createIndividualProjectOwnerRequest.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createIndividualProjectOwnerRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        ProjectOwner projectOwner = createIndividualProjectOwnerRequest.toIndividualProjectOwner();
        if (createIndividualProjectOwnerRequest.profilePicture() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            createIndividualProjectOwnerRequest.profilePicture(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    createIndividualProjectOwnerRequest.profilePicture()
                            )
                    )
            );
        }
        if (createIndividualProjectOwnerRequest.biometricCard() != null) {
            projectOwner.setBiometricCard(
                    uploadService.uploadFile(
                            createIndividualProjectOwnerRequest.biometricCard(),
                            UUID.randomUUID().toString(),
                            FileType.BIOMETRIC_CARD,
                            uploadService.getFileExtension(
                                    createIndividualProjectOwnerRequest.biometricCard()
                            )
                    )
            );
        }
        if (createIndividualProjectOwnerRequest.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            createIndividualProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.BANK_STATEMENT,
                            uploadService.getFileExtension(
                                    createIndividualProjectOwnerRequest.bankStatement()
                            )
                    )
            );
        }
        if (createIndividualProjectOwnerRequest.residenceCertificate() != null) {
            projectOwner.setResidenceCertificate(
                    uploadService.uploadFile(
                            createIndividualProjectOwnerRequest.residenceCertificate(),
                            UUID.randomUUID().toString(),
                            FileType.RESIDENCE,
                            uploadService.getFileExtension(
                                    createIndividualProjectOwnerRequest.residenceCertificate()
                            )
                    )
            );
        }
        ProjectOwner projectOwnerToReturn = projectOwnerRepository.save(projectOwner);
        sendPendingCreationEmail(projectOwnerToReturn.getEmail());
        validationRequestRepository.save(
                ValidationRequest.builder()
                        .id(null)
                        .owner(projectOwnerToReturn)
                        .date(LocalDateTime.now())
                        .state(ValidationState.PENDING)
                        .build()
        );
        return projectOwnerToReturn.toIndividualResponse();
    }

    @Transactional
    public AssociationProjectOwnerResponse createAssociationProjectOwner(
            CreateAssociationProjectOwnerRequest createAssociationProjectOwnerRequest
    ) throws BadRequestException {
        if (userRepository.findByEmail(createAssociationProjectOwnerRequest.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createAssociationProjectOwnerRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        ProjectOwner projectOwner = createAssociationProjectOwnerRequest.toAssociationProjectOwner();
        if (createAssociationProjectOwnerRequest.logo() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            createAssociationProjectOwnerRequest.logo(),
                            UUID.randomUUID().toString(),
                            FileType.LOGO,
                            uploadService.getFileExtension(
                                    createAssociationProjectOwnerRequest.logo()
                            )
                    )
            );
        }
        if (createAssociationProjectOwnerRequest.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            createAssociationProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.BANK_STATEMENT,
                            uploadService.getFileExtension(
                                    createAssociationProjectOwnerRequest.bankStatement()
                            )
                    )
            );
        }
        if (createAssociationProjectOwnerRequest.associationStatus() != null) {
            projectOwner.setAssociationStatus(
                    uploadService.uploadFile(
                            createAssociationProjectOwnerRequest.associationStatus(),
                            UUID.randomUUID().toString(),
                            FileType.ASSOCIATION_STATUS,
                            uploadService.getFileExtension(
                                    createAssociationProjectOwnerRequest.associationStatus()
                            )
                    )
            );
        }
        ProjectOwner projectOwnerToReturn = projectOwnerRepository.save(projectOwner);
        sendPendingCreationEmail(projectOwnerToReturn.getEmail());
        validationRequestRepository.save(
                ValidationRequest.builder()
                        .id(null)
                        .owner(projectOwnerToReturn)
                        .date(LocalDateTime.now())
                        .state(ValidationState.PENDING)
                        .build()
        );
        return projectOwnerToReturn.toAssociationResponse();
    }

    @Transactional
    public OrganizationProjectOwnerResponse createOrganization(
            CreateOrganizationProjectOwnerRequest createOrganizationProjectOwnerRequest
    ) throws BadRequestException {
        if (userRepository.findByEmail(createOrganizationProjectOwnerRequest.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createOrganizationProjectOwnerRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        ProjectOwner projectOwner = createOrganizationProjectOwnerRequest.toOrganizationProjectOwner();
        if (createOrganizationProjectOwnerRequest.logo() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            createOrganizationProjectOwnerRequest.logo(),
                            UUID.randomUUID().toString(),
                            FileType.LOGO,
                            uploadService.getFileExtension(
                                    createOrganizationProjectOwnerRequest.logo()
                            )
                    )
            );
        }
        if (createOrganizationProjectOwnerRequest.rccm() != null) {
            projectOwner.setRccm(
                    uploadService.uploadFile(
                            createOrganizationProjectOwnerRequest.rccm(),
                            UUID.randomUUID().toString(),
                            FileType.RCCM,
                            uploadService.getFileExtension(
                                    createOrganizationProjectOwnerRequest.rccm()
                            )
                    )
            );
        }
        if (createOrganizationProjectOwnerRequest.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            createOrganizationProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.BANK_STATEMENT,
                            uploadService.getFileExtension(
                                    createOrganizationProjectOwnerRequest.bankStatement()
                            )
                    )
            );
        }
        ProjectOwner projectOwnerToReturn = projectOwnerRepository.save(projectOwner);
        sendPendingCreationEmail(projectOwnerToReturn.getEmail());
        validationRequestRepository.save(
                ValidationRequest.builder()
                        .id(null)
                        .owner(projectOwnerToReturn)
                        .date(LocalDateTime.now())
                        .state(ValidationState.PENDING)
                        .build()
        );
        return projectOwnerToReturn.toOrganizationResponse();
    }

    public void sendPendingCreationEmail(
            String recipientEmail
    ) {
        emailService.sendEmail(
                recipientEmail,
                "Création de compte",
                """
                        Bonjour, nous procédons à la vérification de vos informations pour la création de votre compte.
                        Un mail vous sera envoyé prochainement sous 48h pour vous informer de l'état.
                        Merci de bien vouloir patienter, l'équipe de M3Fund.
                        """
        );
    }

    public IndividualProjectOwnerResponse updateIndividualProjectOwner(
            Long demanderId,
            UpdateIndividualProjectOwnerRequest updateIndividualProjectOwnerRequest
    ) throws BadRequestException {
        var projectOwner = projectOwnerRepository.findById(demanderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (projectOwner.getType() != ProjectOwnerType.INDIVIDUAL) {
            throw new BadRequestException("Impossible de faire la modification");
        }
        if (updateIndividualProjectOwnerRequest.firstName() != null) {
            projectOwner.setFirstName(updateIndividualProjectOwnerRequest.firstName());
        }
        if (updateIndividualProjectOwnerRequest.lastName() != null) {
            projectOwner.setLastName(updateIndividualProjectOwnerRequest.lastName());
        }
        if (updateIndividualProjectOwnerRequest.password() != null) {
            projectOwner.setPassword(
                    BCrypt.hashpw(updateIndividualProjectOwnerRequest.password(), BCrypt.gensalt())
            );
        }
        if (updateIndividualProjectOwnerRequest.email() != null) {
            if (userRepository.findByEmail(updateIndividualProjectOwnerRequest.email()).isPresent()) {
                throw new BadRequestException("Email indisponible, choisissez en un autre.");
            }
            projectOwner.setEmail(updateIndividualProjectOwnerRequest.email());
        }
        if (updateIndividualProjectOwnerRequest.phone() != null) {
            if (userRepository.findByPhone(updateIndividualProjectOwnerRequest.phone()).isPresent()) {
                throw new BadRequestException("Numéro de téléphone indisponible, choisissez en un autre.");
            }
            projectOwner.setPhone(updateIndividualProjectOwnerRequest.phone());
        }

        if (updateIndividualProjectOwnerRequest.address() != null) {
            projectOwner.setAddress(updateIndividualProjectOwnerRequest.address());
        }
        if (updateIndividualProjectOwnerRequest.annualIncome() != null) {
            projectOwner.setAnnualIncome(updateIndividualProjectOwnerRequest.annualIncome());
        }
        if (updateIndividualProjectOwnerRequest.profilePicture() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            updateIndividualProjectOwnerRequest.profilePicture(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateIndividualProjectOwnerRequest.profilePicture()
                            )
                    )
            );
        }
        if (updateIndividualProjectOwnerRequest.biometricCard() != null) {
            projectOwner.setBiometricCard(
                    uploadService.uploadFile(
                            updateIndividualProjectOwnerRequest.biometricCard(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateIndividualProjectOwnerRequest.biometricCard()
                            )
                    )
            );
        }
        if (updateIndividualProjectOwnerRequest.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            updateIndividualProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateIndividualProjectOwnerRequest.bankStatement()
                            )
                    )
            );
        }
        if (updateIndividualProjectOwnerRequest.residenceCertificate() != null) {
            projectOwner.setResidenceCertificate(
                    uploadService.uploadFile(
                            updateIndividualProjectOwnerRequest.residenceCertificate(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateIndividualProjectOwnerRequest.residenceCertificate()
                            )
                    )
            );
        }
        return projectOwnerRepository.save(projectOwner).toIndividualResponse();
    }
}
