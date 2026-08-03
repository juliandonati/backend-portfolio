package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface SkillService extends PortfolioComponentService<SkillDto>  {
    List<SkillDto> findSkillsByOwnerUsername(String username);
    String findOwnerUsernameBySkillId(Long id) throws ResourceNotFoundException;
    Optional<String> findOptionalImgUrlBySkillId(Long id);
    void deleteById(Long id) throws ResourceNotFoundException;
}
