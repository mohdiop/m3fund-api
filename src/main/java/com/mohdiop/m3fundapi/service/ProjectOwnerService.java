package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateAssociationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateOrganizationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.OrganizationProjectOwnerResponse;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectOwnerService {

    private final ProjectOwnerRepository projectOwnerRepository;
    private final UserRepository userRepository;

    private final UploadService uploadService;
    private final EmailService emailService;

    public ProjectOwnerService(ProjectOwnerRepository projectOwnerRepository, UserRepository userRepository, UploadService uploadService, EmailService emailService) {
        this.projectOwnerRepository = projectOwnerRepository;
        this.userRepository = userRepository;
        this.uploadService = uploadService;
        this.emailService = emailService;
    }

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
        return projectOwnerToReturn.toIndividualResponse();
    }

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
        return projectOwnerToReturn.toAssociationResponse();
    }

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
        return projectOwnerToReturn.toOrganizationResponse();
    }

    public void sendPendingCreationEmail(
            String recipientEmail
    ) {
        emailService.sendEmail(
                recipientEmail,
                "Création de compte",
                """
                        Bonjour, nous procédons à la vérification de vos informations pour la création de votre compte.\
                        Un mail vous sera envoyé prochainement sous 48h pour vous informer de l'état.
                        Merci de bien vouloir patienter, l'équipe de M3Fund.
                        """
        );
    }
}
