package com.mohdiop.m3fundapi.job;

import com.mohdiop.m3fundapi.repository.CapitalPurchaseRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UnvalidatedCapitalPurchaseDeletionJob {
    private final CapitalPurchaseRepository capitalPurchaseRepository;

    public UnvalidatedCapitalPurchaseDeletionJob(CapitalPurchaseRepository capitalPurchaseRepository) {
        this.capitalPurchaseRepository = capitalPurchaseRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void finishCampaigns() {
        capitalPurchaseRepository.deleteOldUnvalidatedPurchases(
                LocalDateTime.now().minusWeeks(1)
        );
    }
}
