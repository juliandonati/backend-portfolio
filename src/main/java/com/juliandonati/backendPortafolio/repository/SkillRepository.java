package com.juliandonati.backendPortafolio.repository;

import com.juliandonati.backendPortafolio.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query("SELECT skills FROM User u " +
            "JOIN u.ownedPortfolio AS p " +
            "JOIN p.skills AS skills " +
            "WHERE u.username = :username")
    List<Skill> findByOwnerUsername(@Param("username") String username);

    @Query("SELECT owner.username FROM Skill s " +
            "JOIN s.portfolio AS p " +
            "JOIN p.owner AS owner " +
            "WHERE s.id = :id")
    Optional<String> findOwnerUsernameBySkillId(@Param("id") Long id);

    @Query("SELECT s.imgUrl from Skill s " +
            "WHERE s.id = :id")
    Optional<String> findImgUrlBySkillId(@Param("id") Long id);

    @Query("SELECT CASE WHEN count(skillOwner) > 0 THEN TRUE ELSE FALSE END FROM Skill s " +
            "JOIN s.portfolio AS p " +
            "JOIN p.owner AS skillOwner " +
            "WHERE s.id = :id and skillOwner.username = :username ")
    boolean isSkillByIdOwnedByUsername(@Param("id")  Long id, @Param("username") String username);
}
