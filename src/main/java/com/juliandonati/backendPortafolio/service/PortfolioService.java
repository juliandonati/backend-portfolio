package com.juliandonati.backendPortafolio.service;


import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.security.domain.User;

public interface PortfolioService {
    Portfolio findById(long id);
    Portfolio findByOwnerUsername(String username);
    boolean existsByOwnerUsername(String username);
    Portfolio save(Portfolio portfolio);
    Portfolio update(Portfolio portfolio, Long id);

    void deleteById(long id);
    void deleteAboutMeById(long aboutMeId);
    void deletePresentationById(long presentationId);
}
