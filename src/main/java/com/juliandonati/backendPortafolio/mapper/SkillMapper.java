package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.Skill;
import com.juliandonati.backendPortafolio.dto.SkillDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true)
    Skill toEntity(SkillDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imgUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Skill updateEntity(SkillDto dto, @MappingTarget Skill entity);
}
