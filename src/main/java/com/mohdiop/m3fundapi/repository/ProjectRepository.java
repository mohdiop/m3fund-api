package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
