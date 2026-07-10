package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;

public interface PortfolioComponentService<DTO> {
    List<DTO> findAll();
    DTO findById(Long id) throws ResourceNotFoundException;
    DTO update(DTO dto, Long id) throws ResourceNotFoundException;
}
