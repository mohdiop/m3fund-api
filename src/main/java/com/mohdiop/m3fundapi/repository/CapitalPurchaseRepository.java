package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.CapitalPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapitalPurchaseRepository extends JpaRepository<CapitalPurchase, Long> {
    List<CapitalPurchase> findByContributorId(Long contributorId);
}
