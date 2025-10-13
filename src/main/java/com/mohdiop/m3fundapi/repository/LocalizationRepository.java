package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Localization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizationRepository extends JpaRepository<Long, Localization> {
}
