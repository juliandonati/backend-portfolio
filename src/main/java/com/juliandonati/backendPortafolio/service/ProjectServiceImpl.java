package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Project;
import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.ProjectMapper;
import com.juliandonati.backendPortafolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findProjectsByOwnerUsername(String username) {
        return projectRepository.findByOwnerUsername(username).stream().map(projectMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findOptionalImgUrlByProjectId(Long id){
        return projectRepository.findImgUrlByProjectId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public String findOwnerUsernameByProjectId(Long id) throws ResourceNotFoundException {
        return projectRepository.findOwnerUsernameByProjectId(id).orElseThrow(()->new ResourceNotFoundException("No se encontró un proyecto con la id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!projectRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontró un proyecto con la id: " + id);

        projectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        return projectRepository.findAll().stream().map(projectMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) throws ResourceNotFoundException {
        return projectMapper.toDto(
                projectRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No se encontró un proyecto con la id: "+ id)));
    }

    @Override
    @Transactional
    public ProjectDto update(ProjectDto dto, Long id) throws ResourceNotFoundException {
        Project projectToUpdate = projectRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No se encontró un proyecto con la id: "+ id));
        Project updatedProject = projectMapper.updateEntity(dto,projectToUpdate);

        return projectMapper.toDto(projectRepository.save(updatedProject));
    }
}
