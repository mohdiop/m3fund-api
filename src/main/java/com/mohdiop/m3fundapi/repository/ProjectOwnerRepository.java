package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.ProjectOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectOwnerRepository extends JpaRepository<Long, ProjectOwner> {
}
