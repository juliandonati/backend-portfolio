package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.DegreeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;

public interface DegreeService extends PortfolioComponentService<DegreeDto> {
    public List<DegreeDto> findByOwnerUsername(String username);
    public String findImgUrlByDegreeId(Long id) throws ResourceNotFoundException;
    public String findOwnerUsernameByDegreeId(Long id) throws ResourceNotFoundException;
}
