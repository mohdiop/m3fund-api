package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.CapitalPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CapitalPurchaseRepository extends JpaRepository<CapitalPurchase, Long> {
    List<CapitalPurchase> findByContributorId(Long contributorId);

    @Modifying
    @Query("""
                DELETE FROM CapitalPurchase c
                WHERE c.isValidatedByProjectOwner = false
                AND c.date < :oneWeekAgo
            """)
    void deleteOldUnvalidatedPurchases(@Param("oneWeekAgo") LocalDateTime oneWeekAgo);

    List<CapitalPurchase> findByCampaignId(Long campaignId);
}
