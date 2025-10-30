package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    
    @Query("SELECT g FROM Gift g WHERE g.contributor.id = :contributorId ORDER BY g.date DESC")
    List<Gift> findByContributorIdOrderByDateDesc(@Param("contributorId") Long contributorId);
    
    @Query("SELECT g FROM Gift g WHERE g.campaign.project.owner.id = :projectOwnerId ORDER BY g.date DESC")
    List<Gift> findByProjectOwnerIdOrderByDateDesc(@Param("projectOwnerId") Long projectOwnerId);
}
