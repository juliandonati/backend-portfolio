package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.Project;
import com.juliandonati.backendPortafolio.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project entity);

    @Mapping(target = "portfolio", ignore = true)
    Project toEntity(ProjectDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imgUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Project updateEntity(ProjectDto dto, @MappingTarget Project entity);
}
