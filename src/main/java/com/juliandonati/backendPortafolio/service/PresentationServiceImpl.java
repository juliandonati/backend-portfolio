package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Presentation;
import com.juliandonati.backendPortafolio.dto.PresentationDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.PresentationMapper;
import com.juliandonati.backendPortafolio.repository.PresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresentationServiceImpl implements PresentationService {
    private final PresentationRepository presentationRepository;
    private final PresentationMapper presentationMapper;

    @Override
    public List<PresentationDto> findAll() {
        return presentationRepository.findAll().stream().map(presentationMapper::toDto).toList();
    }

    @Override
    public PresentationDto findById(Long id) throws ResourceNotFoundException {
        return presentationMapper.toDto(
                presentationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una presentación con la id: " + id))
        );
    }

    @Override
    public PresentationDto save(PresentationDto dto) {
        return presentationMapper.toDto(
                presentationRepository.save(presentationMapper.toEntity(dto))
        );
    }

    @Override
    public PresentationDto update(PresentationDto dto, Long id) throws ResourceNotFoundException {
        Presentation presentationToUpdate = presentationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró una presentación con la id: " + id));
        Presentation updatedPresentation = presentationMapper.updateEntity(dto, presentationToUpdate);

        return presentationMapper.toDto(
                presentationRepository.save(updatedPresentation)
        );
    }

    @Override
    public PresentationDto findByOwnerUsername(String username) throws ResourceNotFoundException{
        return presentationMapper.toDto(
                presentationRepository.findByOwnerUsername(username).orElseThrow(() -> new ResourceNotFoundException("No se encontró una presentación del usuario: " + username))
        );
    }

    @Override
    public String findImgUrlByOwnerUsername(String username) throws ResourceNotFoundException {
        return presentationRepository.findImgUrlByOwnerUsername(username).orElseThrow(() -> new ResourceNotFoundException("No se encontró una presentación del usuario: " + username));
    }

    @Override
    public boolean existsByOwnerUsername(String username) {
        return presentationRepository.existsByOwnerUsername(username);
    }
}
