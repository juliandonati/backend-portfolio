package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Job;
import com.juliandonati.backendPortafolio.dto.JobDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.JobMapper;
import com.juliandonati.backendPortafolio.mapper.JobMapperImpl;
import com.juliandonati.backendPortafolio.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {
    @Mock
    JobRepository jobRepository;
    @Spy
    JobMapper jobMapper = new JobMapperImpl();

    @InjectMocks
    JobServiceImpl jobService;

    @Test
    void testFindAllJobsReturnsListOfJobs() {
        Long mockId1 = 2L;
        Long mockId2 = 3L;
        String mockName1 = "Google";
        String mockName2 = "Apple";
        List<Job> mockJobs = List.of(
                new Job(mockId1,mockName1,null,null,LocalDate.now(),null,null),
                new Job(mockId2,mockName2,null,null,LocalDate.now(),null,null));
        when(jobRepository.findAll()).thenReturn(mockJobs);

        List<JobDto> result = jobService.findAll();

        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals(mockId1,result.getFirst().getId());
        assertEquals(mockName1,result.getFirst().getName());
        assertEquals(mockId2,result.get(1).getId());
        assertEquals(mockName2,result.get(1).getName());
        verify(jobRepository,times(1)).findAll();
        verify(jobMapper,times(2)).toDto(any(Job.class));
    }

    @Test
    void testFindJobByIdReturnsJob() {
        Long mockId = 1L;
        String mockName = "Globant";
        Job mockJob = new Job(mockId,mockName,null,null,LocalDate.now(),null,null);
        when(jobRepository.findById(mockId)).thenReturn(Optional.of(mockJob));

        JobDto result = jobService.findById(mockId);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockName,result.getName());
        verify(jobRepository,times(1)).findById(mockId);
        verify(jobMapper,times(1)).toDto(mockJob);
    }

    @Test
    void testFindJobByIdThrowsResourceNotFoundException(){
        Long mockId = 999L;
        when(jobRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->jobService.findById(mockId));
        verify(jobRepository,times(1)).findById(mockId);
        verify(jobMapper,never()).toDto(any(Job.class));
    }

    @Test
    void testUpdateJobUpdatesJobSuccessfully() {
        Long mockId= 2L;
        String mockOldName = "SoftwareMakerss";
        String mockPosition = "Asistente";
        LocalDate mockStartDate = LocalDate.now();
        Job mockOldJob = new Job(mockId,mockOldName,mockPosition,null,mockStartDate,null,null);
        when(jobRepository.findById(mockId)).thenReturn(Optional.of(mockOldJob));
        String mockNewName = "SoftwareMakers";
        Job mockUpdatedJob = new Job(mockId,mockNewName,mockPosition,null,mockStartDate,null,null);
        when(jobRepository.save(any(Job.class))).thenReturn(mockUpdatedJob);

        JobDto mockNewJob = new JobDto(null,mockNewName,mockPosition,null,mockStartDate,null);
        JobDto result = jobService.update(mockNewJob,mockId);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockNewName,result.getName());
        assertEquals(mockPosition,result.getPosition());
        assertEquals(mockStartDate,result.getStartDate());
        verify(jobMapper,times(1)).updateEntity(mockNewJob,mockOldJob);
        verify(jobRepository,times(1)).save(any(Job.class));
        verify(jobMapper,times(1)).toDto(mockUpdatedJob);
    }

    @Test
    void testUpdateJobThrowsResourceNotFoundException(){
        Long mockId = 999L;
        JobDto mockNewJobDto = new JobDto();
        when(jobRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->jobService.update(mockNewJobDto,mockId));
        verify(jobRepository,times(1)).findById(mockId);
        verify(jobMapper,never()).updateEntity(any(JobDto.class),any(Job.class));
        verify(jobMapper,never()).toDto(any(Job.class));
    }

    @Test
    void testDeleteJobByIdDeletesJobSuccessfully() {
        Long mockId = 45L;
        when(jobRepository.existsById(mockId)).thenReturn(true);

        assertDoesNotThrow(()->jobService.deleteById(mockId),"El método falló y lanzó una excepción, debería haber terminado con éxito silenciosamente");
        verify(jobRepository,times(1)).existsById(mockId);
        verify(jobRepository,times(1)).deleteById(mockId);
    }

    @Test
    void testDeleteJobByIdThrowsResourceNotFoundException(){
        Long mockId = 999L;
        when(jobRepository.existsById(mockId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->jobService.deleteById(mockId));
        verify(jobRepository,times(1)).existsById(mockId);
        verify(jobRepository,never()).deleteById(any(Long.class));
    }

    @Test
    void testFindJobByOwnerUsernameReturnsListOfJobs() {
        String ownerUsername = "maria32333";
        Long mockId1 = 1L;
        Long mockId2 = 2L;
        String mockName1 = "Accenture";
        String mockName2 = "Apple";
        List<Job> mockJobs = List.of(
                new Job(mockId1,mockName1,null,null,LocalDate.now(),null,null),
                new Job(mockId2,mockName2,null,null,LocalDate.now(),null,null)
                );
        when(jobRepository.findByOwnerUsername(ownerUsername)).thenReturn(mockJobs);

        List<JobDto> result = jobService.findByOwnerUsername(ownerUsername);

        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals(mockId1,result.getFirst().getId());
        assertEquals(mockName1,result.getFirst().getName());
        assertEquals(mockId2,result.get(1).getId());
        assertEquals(mockName2,result.get(1).getName());
        verify(jobRepository,times(1)).findByOwnerUsername(ownerUsername);
        verify(jobMapper,times(2)).toDto(any(Job.class));
    }
}