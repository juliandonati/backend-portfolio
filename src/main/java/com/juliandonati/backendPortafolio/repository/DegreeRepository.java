package com.juliandonati.backendPortafolio.repository;

import com.juliandonati.backendPortafolio.domain.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DegreeRepository extends JpaRepository<Degree, Long> {

    @Override
    @Query("SELECT d FROM Degree d " +
            "WHERE d.id = :id")
    Optional<Degree> findById(@Param("id") Long id);

    @Override
    @Query("SELECT d FROM Degree d")
    List<Degree> findAll();

    @Query("SELECT d.imgUrl from Degree d " +
            "WHERE d.id = :id")
    Optional<String> findImgUrlByDegreeId(@Param("id") Long id);

    @Query("SELECT owner.username FROM Degree d " +
            " JOIN d.portfolio AS p " +
            " JOIN p.owner AS owner " +
            " WHERE d.id = :id")
    Optional<String> findOwnerUsernameByDegreeId(@Param("id") Long id);

    @Query("SELECT degrees FROM User u " +
            "JOIN u.ownedPortfolio AS p " +
            "JOIN p.degrees AS degrees " +
            "WHERE u.username = :username")
    List<Degree> findByOwnerUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN count(degreeOwner) > 0 THEN TRUE ELSE FALSE END FROM Degree d " +
            "JOIN d.portfolio AS p " +
            "JOIN p.owner AS degreeOwner " +
            "WHERE d.id = :id and degreeOwner.username = :username")
    boolean isDegreeByIdOwnedByUsername(@Param("id")  Long id, @Param("username") String username);
}
