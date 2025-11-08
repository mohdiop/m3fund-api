package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    
    /**
     * Trouve toutes les campagnes en cours dont la date de fin est passée
     */
    @Query("SELECT c FROM Campaign c WHERE c.state = :state AND c.endAt < :now")
    List<Campaign> findExpiredCampaigns(@Param("state") CampaignState state, @Param("now") LocalDateTime now);
}
