package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;

public interface ProjectService extends PortfolioComponentService<ProjectDto> {
    List<ProjectDto> findProjectsByOwnerUsername(String username);
    void deleteById(Long id) throws ResourceNotFoundException;
}
