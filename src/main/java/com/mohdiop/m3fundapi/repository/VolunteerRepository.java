package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByContributorIdAndCampaignId(Long contributorId, Long campaignId);
}
