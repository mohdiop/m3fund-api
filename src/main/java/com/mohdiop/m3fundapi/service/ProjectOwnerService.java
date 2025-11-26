package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateAssociationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.create.CreateOrganizationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateAssociationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateIndividualProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateOrganizationProjectOwnerRequest;
import com.mohdiop.m3fundapi.dto.response.AssociationProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.IndividualProjectOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.OrganizationProjectOwnerResponse;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.ValidationRequest;
import com.mohdiop.m3fundapi.entity.enums.*;
import com.mohdiop.m3fundapi.repository.ProjectOwnerRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import com.mohdiop.m3fundapi.repository.ValidationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
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
                        .entity(EntityName.USER)
                        .type(ValidationType.CREATION)
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
                        .entity(EntityName.USER)
                        .type(ValidationType.CREATION)
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
                        .entity(EntityName.USER)
                        .type(ValidationType.CREATION)
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

        var changed = false;

        if (updateIndividualProjectOwnerRequest.firstName() != null
        && !updateIndividualProjectOwnerRequest.firstName().equals(projectOwner.getFirstName())) {
            projectOwner.setFirstName(updateIndividualProjectOwnerRequest.firstName());
            changed = true;
        }
        if (updateIndividualProjectOwnerRequest.lastName() != null
        && !updateIndividualProjectOwnerRequest.lastName().equals(projectOwner.getLastName())) {
            projectOwner.setLastName(updateIndividualProjectOwnerRequest.lastName());
            changed = true;
        }
        if (updateIndividualProjectOwnerRequest.password() != null
        && !BCrypt.checkpw(updateIndividualProjectOwnerRequest.password(), projectOwner.getPassword())) {
            projectOwner.setPassword(
                    BCrypt.hashpw(updateIndividualProjectOwnerRequest.password(), BCrypt.gensalt())
            );
            changed = true;
        }
        if (updateIndividualProjectOwnerRequest.email() != null
        && !updateIndividualProjectOwnerRequest.email().equals(projectOwner.getEmail())) {
            if (userRepository.findByEmail(updateIndividualProjectOwnerRequest.email()).isPresent()) {
                throw new BadRequestException("Email indisponible, choisissez en un autre.");
            }
            projectOwner.setEmail(updateIndividualProjectOwnerRequest.email());
            changed = true;
        }
        if (updateIndividualProjectOwnerRequest.phone() != null
        && !updateIndividualProjectOwnerRequest.phone().equals(projectOwner.getPhone())) {
            if (userRepository.findByPhone(updateIndividualProjectOwnerRequest.phone()).isPresent()) {
                throw new BadRequestException("Numéro de téléphone indisponible, choisissez en un autre.");
            }
            projectOwner.setPhone(updateIndividualProjectOwnerRequest.phone());
            changed = true;
        }

        if (updateIndividualProjectOwnerRequest.address() != null
        && !updateIndividualProjectOwnerRequest.address().equals(projectOwner.getAddress())) {
            projectOwner.setAddress(updateIndividualProjectOwnerRequest.address());
            changed = true;
        }
        if (updateIndividualProjectOwnerRequest.annualIncome() != null
        && updateIndividualProjectOwnerRequest.annualIncome() != projectOwner.getAnnualIncome()) {
            projectOwner.setAnnualIncome(updateIndividualProjectOwnerRequest.annualIncome());
            changed = true;
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
            changed = true;
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
            changed = true;
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
            changed = true;
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
            changed = true;
        }

        if(changed && projectOwner.getState() != UserState.PENDING_VALIDATION) {
            projectOwner.setState(UserState.PENDING_VALIDATION);
            validationRequestRepository.save(
                    ValidationRequest.builder()
                            .id(null)
                            .owner(projectOwner)
                            .state(ValidationState.PENDING)
                            .type(ValidationType.MODIFICATION)
                            .entity(EntityName.USER)
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        return projectOwnerRepository.save(projectOwner).toIndividualResponse();
    }

    /**
     * Met à jour un ProjectOwner de type ORGANISATION en appliquant le patch.
     *
     * @param demanderId L'ID de l'Organisation à mettre à jour.
     * @param updateOrganizationProjectOwnerRequest L'objet DTO contenant les champs potentiellement mis à jour.
     * @return OrganizationProjectOwnerResponse après la mise à jour.
     * @throws EntityNotFoundException Si l'utilisateur n'est pas trouvé.
     * @throws BadRequestException Si le type de ProjectOwner est incorrect ou si un champ unique est déjà utilisé.
     */
    public OrganizationProjectOwnerResponse updateOrganizationProjectOwner(
            Long demanderId,
            UpdateOrganizationProjectOwnerRequest updateOrganizationProjectOwnerRequest
    ) throws BadRequestException, EntityNotFoundException { // Assurez-vous d'avoir l'EntityNotFoundException

        // 1. Récupération et Vérification de l'Entité
        var projectOwner = projectOwnerRepository.findById(demanderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur/Organisation introuvable.")
                );

        // Vérification du type d'entité
        if (projectOwner.getType() != ProjectOwnerType.ORGANIZATION) {
            throw new BadRequestException("Impossible de faire la modification. L'ID ne correspond pas à une organisation.");
        }

        var changed = false;

        // --- Champs Spécifiques à l'Organisation ---

        // 2. Mise à jour du Nom de l'Entité (entityName)
        if (updateOrganizationProjectOwnerRequest.entityName() != null
                && !updateOrganizationProjectOwnerRequest.entityName().equals(projectOwner.getEntityName())) {
            projectOwner.setEntityName(updateOrganizationProjectOwnerRequest.entityName());
            changed = true;
        }

        // 3. Mise à jour du Capital Social (shareCapital)
        if (updateOrganizationProjectOwnerRequest.shareCapital() != null
                // Important : Utiliser Objects.equals pour les Doubles wrappers ou comparer de manière sécurisée.
                // L'opérateur != sur Double peut être problématique.
                && !Objects.equals(updateOrganizationProjectOwnerRequest.shareCapital(), projectOwner.getShareCapital())) {
            projectOwner.setShareCapital(updateOrganizationProjectOwnerRequest.shareCapital());
            changed = true;
        }

        // --- Champs Communs ---

        // 4. Mise à jour du Mot de Passe (password)
        if (updateOrganizationProjectOwnerRequest.password() != null
                && !BCrypt.checkpw(updateOrganizationProjectOwnerRequest.password(), projectOwner.getPassword())) {
            projectOwner.setPassword(
                    BCrypt.hashpw(updateOrganizationProjectOwnerRequest.password(), BCrypt.gensalt())
            );
            changed = true;
        }

        // 5. Mise à jour de l'Email (vérification d'unicité)
        if (updateOrganizationProjectOwnerRequest.email() != null
                && !updateOrganizationProjectOwnerRequest.email().equals(projectOwner.getEmail())) {
            if (userRepository.findByEmail(updateOrganizationProjectOwnerRequest.email()).isPresent()) {
                throw new BadRequestException("Email indisponible, choisissez en un autre.");
            }
            projectOwner.setEmail(updateOrganizationProjectOwnerRequest.email());
            changed = true;
        }

        // 6. Mise à jour du Téléphone (vérification d'unicité)
        if (updateOrganizationProjectOwnerRequest.phone() != null
                && !updateOrganizationProjectOwnerRequest.phone().equals(projectOwner.getPhone())) {
            if (userRepository.findByPhone(updateOrganizationProjectOwnerRequest.phone()).isPresent()) {
                throw new BadRequestException("Numéro de téléphone indisponible, choisissez en un autre.");
            }
            projectOwner.setPhone(updateOrganizationProjectOwnerRequest.phone());
            changed = true;
        }

        // 7. Mise à jour de l'Adresse (address)
        if (updateOrganizationProjectOwnerRequest.address() != null
                && !updateOrganizationProjectOwnerRequest.address().equals(projectOwner.getAddress())) {
            projectOwner.setAddress(updateOrganizationProjectOwnerRequest.address());
            changed = true;
        }

        // 8. Mise à jour du Revenu Annuel (annualIncome)
        if (updateOrganizationProjectOwnerRequest.annualIncome() != null
                && !Objects.equals(updateOrganizationProjectOwnerRequest.annualIncome(), projectOwner.getAnnualIncome())) {
            projectOwner.setAnnualIncome(updateOrganizationProjectOwnerRequest.annualIncome());
            changed = true;
        }

        // --- Mises à jour des Fichiers de l'Organisation ---

        // 9. Mise à jour du Logo (logo)
        if (updateOrganizationProjectOwnerRequest.logo() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            updateOrganizationProjectOwnerRequest.logo(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateOrganizationProjectOwnerRequest.logo()
                            )
                    )
            );
            changed = true;
        }

        // 10. Mise à jour du RCCM (rccm)
        if (updateOrganizationProjectOwnerRequest.rccm() != null) {
            // Le FileType est PICTURE ici, mais pourrait être DOCUMENT si le RCCM est un PDF
            projectOwner.setRccm(
                    uploadService.uploadFile(
                            updateOrganizationProjectOwnerRequest.rccm(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateOrganizationProjectOwnerRequest.rccm()
                            )
                    )
            );
            changed = true;
        }

        // 11. Mise à jour du Relevé Bancaire (bankStatement)
        if (updateOrganizationProjectOwnerRequest.bankStatement() != null) {
            // Le FileType ici devrait probablement être DOCUMENT
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            updateOrganizationProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateOrganizationProjectOwnerRequest.bankStatement()
                            )
                    )
            );
            changed = true;
        }

        // --- Logique Post-Modification ---

        if(changed && projectOwner.getState() != UserState.PENDING_VALIDATION) {
            // Si quelque chose a été modifié, définir l'état en PENDING_VALIDATION
            projectOwner.setState(UserState.PENDING_VALIDATION);

            // Créer la nouvelle demande de validation
            validationRequestRepository.save(
                    ValidationRequest.builder()
                            .id(null)
                            .owner(projectOwner)
                            .state(ValidationState.PENDING)
                            .type(ValidationType.MODIFICATION)
                            .entity(EntityName.USER) // Ou EntityName.ORGANIZATION si pertinent
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        // Sauvegarde et conversion en DTO de réponse
        // Assurez-vous que ProjectOwner a une méthode toOrganizationResponse()
        return projectOwnerRepository.save(projectOwner).toOrganizationResponse();
    }

    /**
     * Met à jour un ProjectOwner de type ASSOCIATION en appliquant le patch.
     *
     * @param demanderId L'ID de l'Association à mettre à jour.
     * @param updateAssociationProjectOwnerRequest L'objet DTO contenant les champs potentiellement mis à jour.
     * @return AssociationProjectOwnerResponse après la mise à jour.
     */
    public AssociationProjectOwnerResponse updateAssociationProjectOwner(
            Long demanderId,
            UpdateAssociationProjectOwnerRequest updateAssociationProjectOwnerRequest
    ) throws BadRequestException, EntityNotFoundException {

        // 1. Récupération et Vérification de l'Entité
        var projectOwner = projectOwnerRepository.findById(demanderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur/Association introuvable.")
                );

        // Vérification du type d'entité
        if (projectOwner.getType() != ProjectOwnerType.ASSOCIATION) {
            throw new BadRequestException("Impossible de faire la modification. L'ID ne correspond pas à une association.");
        }

        var changed = false;

        // --- Champs Spécifiques à l'Association/Organisation ---

        // 2. Mise à jour du Nom de l'Entité (entityName)
        if (updateAssociationProjectOwnerRequest.entityName() != null
                && !updateAssociationProjectOwnerRequest.entityName().equals(projectOwner.getEntityName())) {
            projectOwner.setEntityName(updateAssociationProjectOwnerRequest.entityName());
            changed = true;
        }

        // 3. Mise à jour du Capital Social (shareCapital)
        // Utilisation de Objects.equals pour une comparaison sécurisée des Doubles
        if (updateAssociationProjectOwnerRequest.shareCapital() != null
                && !Objects.equals(updateAssociationProjectOwnerRequest.shareCapital(), projectOwner.getShareCapital())) {
            projectOwner.setShareCapital(updateAssociationProjectOwnerRequest.shareCapital());
            changed = true;
        }

        // --- Champs Communs ---

        // 4. Mise à jour du Mot de Passe (password)
        // On doit vérifier si le nouveau mot de passe est différent avant de le hacher
        if (updateAssociationProjectOwnerRequest.password() != null
                && !BCrypt.checkpw(updateAssociationProjectOwnerRequest.password(), projectOwner.getPassword())) {
            projectOwner.setPassword(
                    BCrypt.hashpw(updateAssociationProjectOwnerRequest.password(), BCrypt.gensalt())
            );
            changed = true;
        }

        // 5. Mise à jour de l'Email (vérification d'unicité)
        if (updateAssociationProjectOwnerRequest.email() != null
                && !updateAssociationProjectOwnerRequest.email().equals(projectOwner.getEmail())) {
            if (userRepository.findByEmail(updateAssociationProjectOwnerRequest.email()).isPresent()) {
                throw new BadRequestException("Email indisponible, choisissez en un autre.");
            }
            projectOwner.setEmail(updateAssociationProjectOwnerRequest.email());
            changed = true;
        }

        // 6. Mise à jour du Téléphone (vérification d'unicité)
        if (updateAssociationProjectOwnerRequest.phone() != null
                && !updateAssociationProjectOwnerRequest.phone().equals(projectOwner.getPhone())) {
            if (userRepository.findByPhone(updateAssociationProjectOwnerRequest.phone()).isPresent()) {
                throw new BadRequestException("Numéro de téléphone indisponible, choisissez en un autre.");
            }
            projectOwner.setPhone(updateAssociationProjectOwnerRequest.phone());
            changed = true;
        }

        // 7. Mise à jour de l'Adresse (address)
        if (updateAssociationProjectOwnerRequest.address() != null
                && !updateAssociationProjectOwnerRequest.address().equals(projectOwner.getAddress())) {
            projectOwner.setAddress(updateAssociationProjectOwnerRequest.address());
            changed = true;
        }

        // 8. Mise à jour du Revenu Annuel (annualIncome)
        // Utilisation de Objects.equals pour une comparaison sécurisée des Doubles
        if (updateAssociationProjectOwnerRequest.annualIncome() != null
                && !Objects.equals(updateAssociationProjectOwnerRequest.annualIncome(), projectOwner.getAnnualIncome())) {
            projectOwner.setAnnualIncome(updateAssociationProjectOwnerRequest.annualIncome());
            changed = true;
        }

        // --- Mises à jour des Fichiers de l'Association ---

        // 9. Mise à jour du Logo (logo)
        if (updateAssociationProjectOwnerRequest.logo() != null) {
            projectOwner.setProfilePicture(
                    uploadService.uploadFile(
                            updateAssociationProjectOwnerRequest.logo(),
                            UUID.randomUUID().toString(),
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateAssociationProjectOwnerRequest.logo()
                            )
                    )
            );
            changed = true;
        }

        // 10. Mise à jour du Statut de l'Association (associationStatus)
        if (updateAssociationProjectOwnerRequest.associationStatus() != null) {
            projectOwner.setAssociationStatus(
                    uploadService.uploadFile(
                            updateAssociationProjectOwnerRequest.associationStatus(),
                            UUID.randomUUID().toString(),
                            // Ce FileType doit correspondre à ce que vous attendez (PICTURE ou DOCUMENT/PDF)
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateAssociationProjectOwnerRequest.associationStatus()
                            )
                    )
            );
            changed = true;
        }

        // 11. Mise à jour du Relevé Bancaire (bankStatement)
        if (updateAssociationProjectOwnerRequest.bankStatement() != null) {
            projectOwner.setBankStatement(
                    uploadService.uploadFile(
                            updateAssociationProjectOwnerRequest.bankStatement(),
                            UUID.randomUUID().toString(),
                            // Ce FileType doit correspondre à ce que vous attendez (probablement DOCUMENT/PDF)
                            FileType.PICTURE,
                            uploadService.getFileExtension(
                                    updateAssociationProjectOwnerRequest.bankStatement()
                            )
                    )
            );
            changed = true;
        }

        // --- Logique Post-Modification (Validation) ---

        if(changed && projectOwner.getState() != UserState.PENDING_VALIDATION) {
            // Si quelque chose a été modifié, redéfinir l'état en attente de validation
            projectOwner.setState(UserState.PENDING_VALIDATION);

            // Créer la nouvelle demande de validation
            validationRequestRepository.save(
                    ValidationRequest.builder()
                            .id(null)
                            .owner(projectOwner)
                            .state(ValidationState.PENDING)
                            .type(ValidationType.MODIFICATION)
                            .entity(EntityName.USER) // Assurez-vous que le nom de l'entité est correct (USER ou ASSOCIATION)
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        // Sauvegarde en base de données et conversion en DTO de réponse
        return projectOwnerRepository.save(projectOwner).toAssociationResponse();
    }

    public Record getById(
            Long ownerId
    ) {
        var owner = projectOwnerRepository.findById(ownerId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );

        switch (owner.getType()) {
            case INDIVIDUAL -> {
                return owner.toIndividualResponse();
            }
            case ORGANIZATION -> {
                return owner.toOrganizationResponse();
            }
            case ASSOCIATION -> {
                return owner.toAssociationResponse();
            }
        }
        throw new RuntimeException("Un problème inconnu est survenu");
    }
}
