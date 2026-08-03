package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ProjectService extends PortfolioComponentService<ProjectDto> {
    List<ProjectDto> findProjectsByOwnerUsername(String username);
    Optional<String> findOptionalImgUrlByProjectId(Long id);
    String findOwnerUsernameByProjectId(Long id) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
