package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Skill;
import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.SkillMapper;
import com.juliandonati.backendPortafolio.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    public List<SkillDto> findAll() {
        return skillRepository.findAll().stream().map(skillMapper::toDto).toList();
    }

    @Override
    public SkillDto findById(Long id) throws ResourceNotFoundException {
        return skillMapper.toDto(
                skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id))
        );
    }

    @Override
    public SkillDto save(SkillDto skillDto) {
        return skillMapper.toDto(
                skillRepository.save(skillMapper.toEntity(skillDto))
        );
    }

    @Override
    public SkillDto update(SkillDto skillDto, Long id) throws ResourceNotFoundException {
        Skill skillToUpdate = skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id));
        Skill updatedSkill = skillMapper.updateEntity(skillDto, skillToUpdate);

        return skillMapper.toDto(
                skillRepository.save(updatedSkill)
        );
    }

    @Override
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!skillRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontró una habilidad con la id: " + id);

        skillRepository.deleteById(id);
    }

    @Override
    public List<SkillDto> findSkillsByOwnerUsername(String username) {
        return skillRepository.findByOwnerUsername(username).stream().map(skillMapper::toDto).toList();
    }

    @Override
    public String findOwnerUsernameBySkillId(Long id) throws ResourceNotFoundException {
        return skillRepository.findOwnerUsernameBySkillId(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id));
    }

    @Override
    public String findImgUrlBySkillId(Long id) throws ResourceNotFoundException {
        return skillRepository.findImgUrlBySkillId(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id));
    }
}
