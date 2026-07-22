package com.juliandonati.backendPortafolio.security.repository;

import com.juliandonati.backendPortafolio.security.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%',:name,'%')) " +
            "OR LOWER(u.displayName) LIKE LOWER(CONCAT('%',:name,'%'))")
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.ownedPortfolio LEFT JOIN FETCH u.modifiablePortfolios " +
            "WHERE u.username = :username")
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.ownedPortfolio LEFT JOIN FETCH u.modifiablePortfolios " +
            "WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    void deleteByUsername(String username);
    void deleteByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN count(u.ownedPortfolio) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean hasPortfolioByUsername(@Param("username") String username);
}
