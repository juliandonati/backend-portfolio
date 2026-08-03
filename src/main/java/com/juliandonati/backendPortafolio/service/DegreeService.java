package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.DegreeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface DegreeService extends PortfolioComponentService<DegreeDto> {
    public List<DegreeDto> findByOwnerUsername(String username);
    public Optional<String> findOptionalImgUrlByDegreeId(Long id);
    public String findOwnerUsernameByDegreeId(Long id) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
