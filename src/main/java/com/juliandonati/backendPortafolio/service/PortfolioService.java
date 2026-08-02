package com.juliandonati.backendPortafolio.service;


import com.juliandonati.backendPortafolio.domain.Portfolio;

public interface PortfolioService {
    Portfolio findById(long id);
    Portfolio findByOwnerUsername(String username);
    boolean existsByOwnerUsername(String username);
    Portfolio save(Portfolio portfolio);
    Portfolio update(Portfolio portfolio, Long id);

    void deleteById(long id);
    void deleteAboutMeById(long aboutMeId) throws Exception;
    void deletePresentationById(long presentationId) throws Exception;

    void deleteAllPortfolioImagesById(Long id) throws Exception;
}
