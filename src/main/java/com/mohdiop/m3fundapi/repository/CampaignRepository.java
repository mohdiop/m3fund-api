package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Campaign;
import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.CampaignState;
import com.mohdiop.m3fundapi.entity.enums.CampaignType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByTypeAndState(CampaignType type, CampaignState state);

    List<Campaign> findByLaunchedAtAfterAndStateOrderByLaunchedAtDesc(LocalDateTime launchedAt, CampaignState state);

    @Modifying
    @Query("UPDATE Campaign n SET n.state = com.mohdiop.m3fundapi.entity.enums.CampaignState.FINISHED " +
            "WHERE n.endAt < CURRENT_TIMESTAMP AND n.state = com.mohdiop.m3fundapi.entity.enums.CampaignState.IN_PROGRESS")
    void finishCampaigns();

    List<Campaign> findByProjectOwnerId(Long projectOwnerId);

    List<Campaign> findByProjectOwnerIdAndProjectId(Long projectOwnerId, Long projectId);

    List<Campaign> findByProjectOwnerIdAndState(Long projectOwnerId, CampaignState state);
}
