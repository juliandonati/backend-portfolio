package com.juliandonati.backendPortafolio.repository;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query("SELECT u.ownedPortfolio FROM User u " +
            "LEFT JOIN FETCH u.ownedPortfolio.presentation " +
            "LEFT JOIN FETCH u.ownedPortfolio.aboutMe " +
            "LEFT JOIN FETCH u.ownedPortfolio.degrees " +
            "LEFT JOIN FETCH u.ownedPortfolio.skills " +
            "LEFT JOIN FETCH u.ownedPortfolio.experience " +
            "LEFT JOIN FETCH u.ownedPortfolio.authorizedUsers " +
            "WHERE u.username = :username")
    Optional<Portfolio> findByOwnerUsername(String username);

    @Query("SELECT CASE WHEN count(u.ownedPortfolio) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User u " +
            "WHERE u.username = :username")
    boolean existsByOwnerUsername(@Param("username") String username);
}
