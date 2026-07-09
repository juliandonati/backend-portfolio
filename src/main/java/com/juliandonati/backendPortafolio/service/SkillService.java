package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;

import java.util.List;

public interface SkillService extends PortfolioComponentService<SkillDto>  {
    List<SkillDto> findSkillsByOwnerUsername(String username);
    String findOwnerUsernameBySkillId(Long id) throws ResourceNotFoundException;
    String findImgUrlBySkillId(Long id) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
