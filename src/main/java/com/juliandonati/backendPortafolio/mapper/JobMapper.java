package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.Job;
import com.juliandonati.backendPortafolio.dto.JobDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface JobMapper {
    JobDto toDto(Job job);

    @Mapping(target = "portfolio", ignore = true)
    Job toEntity(JobDto dto);

    @Mapping(target = "id", ignore = true)
    Job updateEntity(JobDto dto, @MappingTarget Job entity);
}
