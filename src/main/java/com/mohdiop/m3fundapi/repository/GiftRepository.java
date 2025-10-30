package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByContributorId(Long contributorId);
}
