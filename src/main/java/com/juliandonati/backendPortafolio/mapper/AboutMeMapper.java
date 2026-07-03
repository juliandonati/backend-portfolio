package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AboutMeMapper {
    AboutMeDto toDto(AboutMe entity);

    @Mapping(target="id",ignore=true)
    @Mapping(target="portfolio",ignore=true)
    AboutMe toEntity(AboutMeDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bgImgUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AboutMe updateEntity(AboutMeDto aboutMeDto, @MappingTarget AboutMe entity);
}
