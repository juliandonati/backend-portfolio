package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.Degree;
import com.juliandonati.backendPortafolio.dto.DegreeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DegreeMapper {
    DegreeDto toDto(Degree entity);

    @Mapping(target="id",ignore = true)
    @Mapping(target="portfolio",ignore = true)
    Degree toEntity(DegreeDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imgUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Degree updateEntity(DegreeDto dto, @MappingTarget Degree entity);
}
