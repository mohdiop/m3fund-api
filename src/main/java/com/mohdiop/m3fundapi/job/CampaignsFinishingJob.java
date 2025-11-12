package com.mohdiop.m3fundapi.job;

import com.mohdiop.m3fundapi.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CampaignsFinishingJob {

    private static final Logger logger = LoggerFactory.getLogger(CampaignsFinishingJob.class);
    private final CampaignService campaignService;

    public CampaignsFinishingJob(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *") // S'exécute toutes les minutes
    public void finishCampaigns() {
        logger.debug("Exécution du job de clôture automatique des campagnes");
        try {
            campaignService.finishCampaigns();
            logger.debug("Job de clôture des campagnes terminé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution du job de clôture des campagnes", e);
        }
    }
}
