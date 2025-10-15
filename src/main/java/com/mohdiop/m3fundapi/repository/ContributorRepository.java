package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {
}
