package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Degree;
import com.juliandonati.backendPortafolio.dto.DegreeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.DegreeMapper;
import com.juliandonati.backendPortafolio.repository.DegreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DegreeServiceImpl implements DegreeService {
    private final DegreeRepository degreeRepository;
    private final DegreeMapper degreeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DegreeDto> findAll() {
        return degreeRepository.findAll().stream().map(degreeMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DegreeDto findById(Long id) throws ResourceNotFoundException {
        return degreeMapper.toDto(
                degreeRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No se encontró un título académico de id: " + id))
        );
    }

    @Override
    @Transactional
    public DegreeDto update(DegreeDto degreeDto, Long id) throws ResourceNotFoundException {
        Degree degreeToUpdate = degreeRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No se encontró un título académico de id: " + id));
        Degree updatedDegree = degreeMapper.updateEntity(degreeDto, degreeToUpdate);

        return degreeMapper.toDto(
                degreeRepository.save(updatedDegree)
        );
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!degreeRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontró un título académico de id: " + id);

        degreeRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<DegreeDto> findByOwnerUsername(String username){
        return degreeRepository.findByOwnerUsername(username).stream().map(degreeMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findOptionalImgUrlByDegreeId(Long id){
        return degreeRepository.findImgUrlByDegreeId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public String findOwnerUsernameByDegreeId(Long id) throws ResourceNotFoundException {
        return degreeRepository.findOwnerUsernameByDegreeId(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un título académico de id: " + id));
    }
}
