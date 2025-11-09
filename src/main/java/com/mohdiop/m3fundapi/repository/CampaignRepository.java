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
    
    /**
     * Trouve toutes les campagnes d'un propriétaire de projet
     */
    List<Campaign> findByProjectOwnerId(Long ownerId);
    
    /**
     * Trouve les campagnes d'un propriétaire filtrées par projet
     */
    @Query("SELECT c FROM Campaign c WHERE c.projectOwner.id = :ownerId AND c.project.id = :projectId")
    List<Campaign> findByProjectOwnerIdAndProjectId(@Param("ownerId") Long ownerId, @Param("projectId") Long projectId);
    
    /**
     * Trouve les campagnes d'un propriétaire filtrées par statut
     */
    @Query("SELECT c FROM Campaign c WHERE c.projectOwner.id = :ownerId AND c.state = :state")
    List<Campaign> findByProjectOwnerIdAndState(@Param("ownerId") Long ownerId, @Param("state") CampaignState state);
}
