package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateAssociationProjectOwner;
import com.mohdiop.m3fundapi.dto.request.create.CreateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
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

    public ProjectOwnerService(ProjectOwnerRepository projectOwnerRepository, UserRepository userRepository, UploadService uploadService) {
        this.projectOwnerRepository = projectOwnerRepository;
        this.userRepository = userRepository;
        this.uploadService = uploadService;
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
        return projectOwnerRepository.save(
                projectOwner
        ).toIndividualResponse();
    }

    public AssociationProjectOwnerResponse createAssociationProjectOwner(
            CreateAssociationProjectOwner createAssociationProjectOwner
    ) throws BadRequestException {
        if (userRepository.findByEmail(createAssociationProjectOwner.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createAssociationProjectOwner.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        ProjectOwner projectOwner = createAssociationProjectOwner.toAssociationProjectOwner();
        if (createAssociationProjectOwner.logo() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            createAssociationProjectOwner.logo(),
                            UUID.randomUUID().toString(),
                            FileType.LOGO,
                            uploadService.getFileExtension(
                                    createAssociationProjectOwner.logo()
                            )
                    )
            );
        }
        if (createAssociationProjectOwner.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            createAssociationProjectOwner.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.BANK_STATEMENT,
                            uploadService.getFileExtension(
                                    createAssociationProjectOwner.bankStatement()
                            )
                    )
            );
        }
        if (createAssociationProjectOwner.associationStatus() != null) {
            projectOwner.setAssociationStatus(
                    uploadService.uploadFile(
                            createAssociationProjectOwner.associationStatus(),
                            UUID.randomUUID().toString(),
                            FileType.ASSOCIATION_STATUS,
                            uploadService.getFileExtension(
                                    createAssociationProjectOwner.associationStatus()
                            )
                    )
            );
        }
        return projectOwnerRepository.save(projectOwner).toAssociationResponse();
    }
}
