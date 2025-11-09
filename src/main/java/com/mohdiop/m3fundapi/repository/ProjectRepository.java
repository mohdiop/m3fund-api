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
    
    // Filtrer par propriétaire
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId")
    List<Project> findByOwnerId(@Param("ownerId") Long ownerId);
    
    // Rechercher des projets d'un propriétaire par nom ou description
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.resume) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Project> searchProjectsByOwner(@Param("ownerId") Long ownerId, @Param("searchTerm") String searchTerm);
    
    // Filtrer les projets d'un propriétaire par statut de validation
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND p.isValidated = :isValidated")
    List<Project> findByOwnerIdAndIsValidated(@Param("ownerId") Long ownerId, @Param("isValidated") boolean isValidated);
    
    // Filtrer les projets d'un propriétaire par domaine
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND p.domain = :domain")
    List<Project> findByOwnerIdAndDomain(@Param("ownerId") Long ownerId, @Param("domain") ProjectDomain domain);
}
