package com.mohdiop.m3fundapi.job;

import com.mohdiop.m3fundapi.repository.CampaignRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CampaignsFinishingJob {

    private final CampaignRepository campaignRepository;

    public CampaignsFinishingJob(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void finishCampaigns() {
        campaignRepository.finishCampaigns();
    }
}
