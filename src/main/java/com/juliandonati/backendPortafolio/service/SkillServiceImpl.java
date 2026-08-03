package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Skill;
import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.SkillMapper;
import com.juliandonati.backendPortafolio.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> findAll() {
        return skillRepository.findAll().stream().map(skillMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDto findById(Long id) throws ResourceNotFoundException {
        return skillMapper.toDto(
                skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id))
        );
    }


    @Override
    @Transactional
    public SkillDto update(SkillDto skillDto, Long id) throws ResourceNotFoundException {
        Skill skillToUpdate = skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id));
        Skill updatedSkill = skillMapper.updateEntity(skillDto, skillToUpdate);

        return skillMapper.toDto(
                skillRepository.save(updatedSkill)
        );
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!skillRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontró una habilidad con la id: " + id);

        skillRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> findSkillsByOwnerUsername(String username) {
        return skillRepository.findByOwnerUsername(username).stream().map(skillMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String findOwnerUsernameBySkillId(Long id) throws ResourceNotFoundException {
        return skillRepository.findOwnerUsernameBySkillId(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una habilidad con la id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findOptionalImgUrlBySkillId(Long id){
        return skillRepository.findImgUrlBySkillId(id);
    }
}
