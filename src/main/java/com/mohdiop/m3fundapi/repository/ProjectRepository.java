package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByDomainAndIsValidated(ProjectDomain domain, boolean isValidated);

    List<Project> findByOwnerId(Long ownerId);

    List<Project> findByOwnerIdAndIsValidated(Long ownerId, boolean isValidated);

    List<Project> findByOwnerIdAndDomain(Long ownerId, ProjectDomain domain);

    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.resume) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Project> searchOwnerProjects(@Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm);
}
