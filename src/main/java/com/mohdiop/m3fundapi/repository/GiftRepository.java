package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftRepository extends JpaRepository<Long, Gift> {
}
