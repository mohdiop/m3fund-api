package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByDomainAndIsValidated(ProjectDomain domain, boolean isValidated);
}
