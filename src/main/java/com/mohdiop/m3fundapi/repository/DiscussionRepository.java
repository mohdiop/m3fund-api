package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
}
