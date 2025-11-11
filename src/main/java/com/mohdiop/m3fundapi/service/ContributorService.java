package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateContributorRequest;
import com.mohdiop.m3fundapi.dto.request.update.UpdateContributorRequest;
import com.mohdiop.m3fundapi.dto.response.ContributorResponse;
import com.mohdiop.m3fundapi.dto.response.NotificationResponse;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.Notification;
import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import com.mohdiop.m3fundapi.repository.ContributorRepository;
import com.mohdiop.m3fundapi.repository.NotificationRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ContributorService {

    private final ContributorRepository contributorRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public ContributorService(ContributorRepository contributorRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.contributorRepository = contributorRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public ContributorResponse createContributor(
            CreateContributorRequest createContributorRequest
    ) throws BadRequestException {
        if (userRepository.findByEmail(createContributorRequest.email()).isPresent()) {
            throw new BadRequestException("Email invalide!");
        }
        if (userRepository.findByPhone(createContributorRequest.phone()).isPresent()) {
            throw new BadRequestException("Numéro de téléphone invalide!");
        }
        var contributor = contributorRepository.save(
                createContributorRequest.toContributor()
        ).toResponse();
        sendAccountCreationNotification(contributor.id());
        return contributor;
    }

    public ContributorResponse updateContributor(
            Long id,
            UpdateContributorRequest updateContributorRequest
    ) throws BadRequestException {
        Contributor contributor = contributorRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        if (updateContributorRequest.firstName() != null) {
            contributor.setFirstName(updateContributorRequest.firstName());
        }
        if (updateContributorRequest.lastName() != null) {
            contributor.setLastName(updateContributorRequest.lastName());
        }
        if (updateContributorRequest.projectDomainPrefs() != null) {
            contributor.setProjectDomains(updateContributorRequest.projectDomainPrefs());
        }
        if (updateContributorRequest.campaignTypePrefs() != null) {
            contributor.setCampaignTypes(updateContributorRequest.campaignTypePrefs());
        }
        if (updateContributorRequest.phone() != null) {
            if (userRepository.findByPhone(updateContributorRequest.phone()).isPresent()) {
                throw new BadRequestException("Numéro de téléphone indisponible, choisissez en un autre.");
            }
            contributor.setPhone(updateContributorRequest.phone());
        }
        if (updateContributorRequest.email() != null) {
            if (userRepository.findByEmail(updateContributorRequest.email()).isPresent()) {
                throw new BadRequestException("Email indisponible, choisissez en un autre.");
            }
            contributor.setEmail(updateContributorRequest.email());
        }
        if (updateContributorRequest.password() != null) {
            contributor.setPassword(BCrypt.hashpw(updateContributorRequest.password(), BCrypt.gensalt()));
        }
        if (updateContributorRequest.localization() != null) {
            contributor.getLocalization().setCountry(updateContributorRequest.localization().country());
            contributor.getLocalization().setRegion(updateContributorRequest.localization().region());
            contributor.getLocalization().setTown(updateContributorRequest.localization().town());
            contributor.getLocalization().setStreet(updateContributorRequest.localization().street());
            contributor.getLocalization().setLongitude(updateContributorRequest.localization().longitude());
            contributor.getLocalization().setLatitude(updateContributorRequest.localization().latitude());
        }
        return contributorRepository.save(contributor).toResponse();
    }

    public void sendAccountCreationNotification(
            Long contributorId
    ) {
        var contributor = contributorRepository.findById(contributorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Contributeur introuvable.")
                );
        var system = userRepository.findById(1L)
                .orElseThrow(
                        () -> new RuntimeException("Un problème interne est survenu.")
                );
        String title = "Compte créé";
        String content = "Bienvenue sur M3Fund %s %s, découvrez des projets dans votre secteur et contribuez à leur développement.";
        notificationRepository.save(
                new Notification(
                        null,
                        title,
                        content,
                        system,
                        contributor,
                        LocalDateTime.now(),
                        false,
                        NotificationType.SYSTEM_ALERT
                )
        );
    }
}
