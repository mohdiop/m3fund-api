package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.repository.CampaignRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {

    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;

    /**
     * Ferme automatiquement les campagnes dont la date de fin est passée
     * Exécuté toutes les heures
     */
    @Scheduled(cron = "0 0 * * * ?") // Toutes les heures à la minute 0
    @Transactional
    public void closeExpiredCampaigns() {
        log.info("Début de la vérification des campagnes expirées...");
        
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> expiredCampaigns = campaignRepository.findExpiredCampaigns(
                CampaignState.IN_PROGRESS, now);

        if (expiredCampaigns.isEmpty()) {
            log.info("Aucune campagne expirée trouvée.");
            return;
        }

        int closedCount = 0;
        for (Campaign campaign : expiredCampaigns) {
            campaign.setState(CampaignState.FINISHED);
            campaign.setUpdatedAt(LocalDateTime.now());
            campaignRepository.save(campaign);
            closedCount++;
            log.info("Campagne {} fermée automatiquement (date de fin: {})", 
                    campaign.getId(), campaign.getEndAt());
        }

        log.info("{} campagne(s) fermée(s) automatiquement.", closedCount);
    }

    /**
     * Ferme automatiquement les projets dont toutes les campagnes sont terminées
     * et dont la date de lancement + durée maximale est passée
     * Exécuté toutes les 6 heures
     */
    @Scheduled(cron = "0 0 */6 * * ?") // Toutes les 6 heures
    @Transactional
    public void closeExpiredProjects() {
        log.info("Début de la vérification des projets expirés...");
        
        LocalDateTime now = LocalDateTime.now();
        List<Project> allProjects = projectRepository.findAll();
        
        int closedCount = 0;
        for (Project project : allProjects) {
            // Vérifier si le projet a des campagnes
            if (project.getCampaigns() == null || project.getCampaigns().isEmpty()) {
                continue;
            }

            // Vérifier si toutes les campagnes sont terminées
            boolean allCampaignsFinished = project.getCampaigns().stream()
                    .allMatch(campaign -> 
                        campaign.getState() == CampaignState.FINISHED 
                        || campaign.getState() == CampaignState.PENDING
                    );

            // Si toutes les campagnes sont terminées, on peut considérer le projet comme terminé
            // Note: Les projets n'ont pas de statut "terminé" explicite, 
            // mais on pourrait ajouter une logique ici si nécessaire
            if (allCampaignsFinished) {
                // Vérifier si la date de fin de la dernière campagne est passée
                LocalDateTime latestEndDate = project.getCampaigns().stream()
                        .map(Campaign::getEndAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(project.getLaunchedAt());

                if (latestEndDate.isBefore(now)) {
                    // Le projet est considéré comme terminé
                    // Ici, on pourrait ajouter une logique pour marquer le projet comme terminé
                    // Par exemple, ajouter un champ isCompleted dans Project
                    log.info("Projet {} est terminé (toutes les campagnes sont fermées, dernière date de fin: {})", 
                            project.getId(), latestEndDate);
                    closedCount++;
                }
            }
        }

        if (closedCount > 0) {
            log.info("{} projet(s) considéré(s) comme terminé(s).", closedCount);
        } else {
            log.info("Aucun projet terminé trouvé.");
        }
    }
}

