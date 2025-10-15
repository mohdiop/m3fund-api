package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateProjectOwnerRequest;
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
            CreateProjectOwnerRequest createProjectOwnerRequest
    ) throws BadRequestException {
        if (userRepository.findByEmail(createProjectOwnerRequest.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createProjectOwnerRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        ProjectOwner projectOwner = createProjectOwnerRequest.toIndividualProjectOwner();
        if (createProjectOwnerRequest.profilePicture() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            createProjectOwnerRequest.profilePicture(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    createProjectOwnerRequest.profilePicture()
                            )
                    )
            );
        }
        if (createProjectOwnerRequest.biometricCard() != null) {
            projectOwner.setBiometricCard(
                    uploadService.uploadFile(
                            createProjectOwnerRequest.biometricCard(),
                            UUID.randomUUID().toString(),
                            FileType.BIOMETRIC_CARD,
                            uploadService.getFileExtension(
                                    createProjectOwnerRequest.biometricCard()
                            )
                    )
            );
        }
        if (createProjectOwnerRequest.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            createProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.BANK_STATEMENT,
                            uploadService.getFileExtension(
                                    createProjectOwnerRequest.bankStatement()
                            )
                    )
            );
        }
        if (createProjectOwnerRequest.residenceCertificate() != null) {
            projectOwner.setResidenceCertificate(
                    uploadService.uploadFile(
                            createProjectOwnerRequest.residenceCertificate(),
                            UUID.randomUUID().toString(),
                            FileType.RESIDENCE,
                            uploadService.getFileExtension(
                                    createProjectOwnerRequest.residenceCertificate()
                            )
                    )
            );
        }
        return projectOwnerRepository.save(
                projectOwner
        ).toIndividualResponse();
    }
}
