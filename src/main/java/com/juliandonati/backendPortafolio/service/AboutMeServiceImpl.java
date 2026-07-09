package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.AboutMeMapper;
import com.juliandonati.backendPortafolio.repository.AboutMeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AboutMeServiceImpl implements AboutMeService {
    private final AboutMeRepository aboutMeRepository;
    private final AboutMeMapper aboutMeMapper;

    @Override
    public List<AboutMeDto> findAll() {
        return aboutMeRepository.findAll().stream().map(aboutMeMapper::toDto).toList();
    }

    @Override
    public AboutMeDto findById(Long id) throws ResourceNotFoundException {
        return aboutMeMapper.toDto(aboutMeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un 'SOBRE MÍ' de id: " + id)));
    }

    @Override
    public AboutMeDto save(AboutMeDto aboutMeDto) {
        return aboutMeMapper.toDto(
                aboutMeRepository.save(aboutMeMapper.toEntity(aboutMeDto))
        );
    }

    @Override
    public AboutMeDto update(AboutMeDto aboutMeDto, Long id) throws ResourceNotFoundException {
        AboutMe aboutMeToUpdate = aboutMeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un 'SOBRE MÍ' de id: " + id));
        AboutMe updatedAboutMe = aboutMeMapper.updateEntity(aboutMeDto, aboutMeToUpdate);


        return aboutMeMapper.toDto(
                aboutMeRepository.save(updatedAboutMe)
        );
    }

    @Override
    public AboutMeDto findByOwnerUsername(String username) throws ResourceNotFoundException {
        return aboutMeMapper.toDto(
                aboutMeRepository.findByOwnerUsername(username).orElseThrow(() -> new ResourceNotFoundException("El usuario o el portafolio no existe"))
                );
    }

    @Override
    public boolean existsByOwnerUsername(String username) {
        return aboutMeRepository.existsByOwnerUsername(username);
    }
}
