package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.PresentationDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import org.springframework.data.repository.query.Param;

public interface PresentationService extends PortfolioComponentService<PresentationDto> {
    PresentationDto findByOwnerUsername(String username) throws ResourceNotFoundException;
    String findImgUrlByOwnerUsername(String username) throws ResourceNotFoundException;
    boolean existsByOwnerUsername(String username);
}
