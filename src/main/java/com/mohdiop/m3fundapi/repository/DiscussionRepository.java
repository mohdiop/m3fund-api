package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Optional<Discussion> findByContributorId(Long contributorId);
    Optional<Discussion> findByProjectOwnerId(Long projectOwnerId);
    Optional<Discussion> findByContributorIdOrProjectOwnerId(Long contributorId, Long projectOwnerId);
}
