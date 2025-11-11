package com.mohdiop.m3fundapi.job;

import com.mohdiop.m3fundapi.service.CampaignService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CampaignsFinishingJob {

    private final CampaignService campaignService;

    public CampaignsFinishingJob(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void finishCampaigns() {
        campaignService.finishCampaigns();
    }
}
