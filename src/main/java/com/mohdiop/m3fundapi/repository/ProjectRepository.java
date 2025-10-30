package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Project;
import com.mohdiop.m3fundapi.entity.enums.ProjectDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // Rechercher des projets par nom ou description
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.resume) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Project> searchProjects(@Param("searchTerm") String searchTerm);
    
    // Filtrer par statut de validation
    List<Project> findByIsValidated(boolean isValidated);
    
    // Filtrer par domaine
    List<Project> findByDomain(ProjectDomain domain);
    
    // Filtrer par domaine et statut de validation
    List<Project> findByDomainAndIsValidated(ProjectDomain domain, boolean isValidated);
}
