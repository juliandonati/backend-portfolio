package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;

public interface ProjectService extends PortfolioComponentService<ProjectDto> {
    List<ProjectDto> findProjectsByOwnerUsername(String username);
    String findImgUrlByProjectId(Long id) throws ResourceNotFoundException;
    String findOwnerUsernameByProjectId(Long id) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
