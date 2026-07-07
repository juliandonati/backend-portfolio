package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.Job;
import com.juliandonati.backendPortafolio.dto.JobDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JobMapperTest {
    JobMapper jobMapper = Mappers.getMapper(JobMapper.class);

    @Test
    void testMapJobEntitytoJobDtoSuccessfully() {
        Long mockId = 7L;
        String mockName = "SoftwareDevelopers Inc", mockPosition = "Data Analyst", mockDesc = "Me encargué de cumplir mi trabajo responsablemente.";
        LocalDate mockStartDate = LocalDate.of(2023,10,9);

        Job mockJob = new Job(mockId,mockName,mockPosition,mockDesc,mockStartDate,null,null);

        JobDto result = jobMapper.toDto(mockJob);

        assertAll("Validando los campos tras el mapeo...",
                ()->assertNotNull(result),
                ()->assertEquals(mockId,result.getId()),
                ()->assertEquals(mockName,result.getName()),
                ()->assertEquals(mockPosition,result.getPosition()),
                ()->assertEquals(mockDesc,result.getDescription()),
                ()->assertEquals(mockStartDate,result.getStartDate()),
                ()->assertNull(result.getEndDate())
        );
    }

    @Test
    void testMapJobDtoToJobEntitySuccessfully() {
        Long mockId = 7L;
        String mockName = "SoftwareDevelopers Inc", mockPosition = "Data Analyst", mockDesc = "Me encargué de cumplir mi trabajo responsablemente.";
        LocalDate mockStartDate = LocalDate.of(2023,10,9);

        JobDto mockJobDto = new JobDto(mockId,mockName,mockPosition,mockDesc,mockStartDate,null);

        Job result = jobMapper.toEntity(mockJobDto);

        assertAll("Validando los campos tras el mapeo...",
                ()->assertNotNull(result),
                ()->assertEquals(mockId,result.getId()),
                ()->assertEquals(mockName,result.getName()),
                ()->assertEquals(mockPosition,result.getPosition()),
                ()->assertEquals(mockDesc,result.getDescription()),
                ()->assertEquals(mockStartDate,result.getStartDate()),
                ()->assertNull(result.getEndDate())
        );
    }

    @Test
    void testUpdateJobEntitySuccessfully() {
        Long mockId = 7L;
        String mockOldName = "SoftwareDevelopers Inc", mockOldPosition = "Data Analyst", mockOldDesc = "Me encargué de cumplir mi trabajo responsablemente.";
        LocalDate mockOldStartDate = LocalDate.of(2023,10,9);
        Job mockOldJob = new Job(mockId,mockOldName,mockOldPosition,mockOldDesc,mockOldStartDate,null,null);

        String mockNewName = "SoftwareDevelopers Incorporated", mockNewPosition = "Project Manager", mockNewDesc = "Me encargo de asegurarme que el proyecto avance responsablemente";
        LocalDate mockNewStartDate = LocalDate.of(2023,10,10), mockNewEndDate = LocalDate.of(2026,7,7);
        JobDto mockNewJobDto = new JobDto(mockId,mockNewName,mockNewPosition,mockNewDesc,mockNewStartDate,mockNewEndDate);

        Job result = jobMapper.updateEntity(mockNewJobDto,mockOldJob);

        assertAll("Validando los campos tras el mapeo...",
                ()->assertNotNull(result),
                ()->assertEquals(mockId,result.getId()),
                ()->assertEquals(mockNewName,result.getName()),
                ()->assertEquals(mockNewPosition,result.getPosition()),
                ()->assertEquals(mockNewDesc,result.getDescription()),
                ()->assertEquals(mockNewStartDate,result.getStartDate()),
                ()->assertEquals(mockNewEndDate,result.getEndDate())
        );
    }
}