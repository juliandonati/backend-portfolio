package com.juliandonati.backendPortafolio.repository;

import com.juliandonati.backendPortafolio.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT projects FROM User u " +
            "JOIN u.ownedPortfolio AS portf " +
            "JOIN portf.projects AS projects " +
            "WHERE u.username = :username")
    List<Project> findByOwnerUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN EXISTS " +
            "(SELECT 1 FROM Project p JOIN p.portfolio AS portf JOIN portf.owner AS owner WHERE p.id = :id AND owner.username = :username) " +
            "THEN TRUE ELSE FALSE END")
    boolean isProjectByIdOwnedByUsername(@Param("id") Long id,@Param("username") String username);
}
