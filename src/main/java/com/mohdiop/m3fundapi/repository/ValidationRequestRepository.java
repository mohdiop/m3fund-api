package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.ValidationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationRequestRepository extends JpaRepository<ValidationRequest, Long> {
    Optional<ValidationRequest> findByOwnerId(Long ownerId);

    Optional<ValidationRequest> findByProjectId(Long projectId);
}
