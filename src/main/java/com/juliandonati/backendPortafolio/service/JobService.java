package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.JobDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;

public interface JobService extends PortfolioComponentService<JobDto> {
    List<JobDto> findByOwnerUsername(String username) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
