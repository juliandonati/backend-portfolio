package com.juliandonati.backendPortafolio.repository;

import com.juliandonati.backendPortafolio.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT projects FROM User u " +
            "JOIN u.ownedPortfolio AS portf " +
            "JOIN portf.projects AS projects " +
            "WHERE u.username = :username")
    List<Project> findByOwnerUsername(@Param("username") String username);

    @Query("SELECT p.imgUrl FROM Project p WHERE p.id = :id")
    Optional<String> findImgUrlByProjectId(@Param("id") Long id);

    @Query("SELECT owner.username FROM Project p " +
            "JOIN p.portfolio AS portfolio " +
            "JOIN portfolio.owner AS owner " +
            "WHERE p.id = :id")
    Optional<String> findOwnerUsernameByProjectId(@Param("id") Long id);

    @Query("SELECT CASE WHEN EXISTS " +
            "(SELECT 1 FROM Project p JOIN p.portfolio AS portf JOIN portf.owner AS owner WHERE p.id = :id AND owner.username = :username) " +
            "THEN TRUE ELSE FALSE END")
    boolean isProjectByIdOwnedByUsername(@Param("id") Long id,@Param("username") String username);

    Long id(Long id);
}
