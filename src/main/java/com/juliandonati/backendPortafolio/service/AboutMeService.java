package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

public interface AboutMeService extends PortfolioComponentService<AboutMeDto> {
    AboutMeDto findByOwnerUsername(String username) throws ResourceNotFoundException;
    boolean existsByOwnerUsername(String username);
    AboutMeDto save(AboutMeDto aboutMeDto);
}
