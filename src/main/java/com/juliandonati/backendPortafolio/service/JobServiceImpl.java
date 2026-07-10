package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Job;
import com.juliandonati.backendPortafolio.dto.JobDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.JobMapper;
import com.juliandonati.backendPortafolio.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    public List<JobDto> findAll() {
        return jobRepository.findAll().stream().map(jobMapper::toDto).toList();
    }

    @Override
    public JobDto findById(Long id) throws ResourceNotFoundException {
        return jobMapper.toDto(
                jobRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No se encontró una experiencia laboral con la id: " + id))
        );
    }

    @Override
    public JobDto update(JobDto jobDto, Long id) throws ResourceNotFoundException {
        Job jobToUpdate = jobRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No se encontró una experiencia laboral con la id: " + id));
        Job updatedJob = jobMapper.updateEntity(jobDto, jobToUpdate);

        return jobMapper.toDto(
                jobRepository.save(updatedJob)
        );
    }

    @Override
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!jobRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontró una experiencia laboral con la id: " + id);

        jobRepository.deleteById(id);
    }

    @Override
    public List<JobDto> findByOwnerUsername(String username) throws ResourceNotFoundException {
        return jobRepository.findByOwnerUsername(username).stream().map(jobMapper::toDto).toList();
    }
}
