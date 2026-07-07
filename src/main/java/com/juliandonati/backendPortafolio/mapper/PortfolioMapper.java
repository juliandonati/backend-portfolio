package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.*;
import com.juliandonati.backendPortafolio.dto.*;
import com.juliandonati.backendPortafolio.security.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {
        PresentationMapper.class,
        AboutMeMapper.class,
        DegreeMapper.class,
        JobMapper.class,
        SkillMapper.class
})
public abstract class PortfolioMapper {
    @Mapping(target = "owner", source = "portfolio.owner", qualifiedByName = "mapOwnerToString")
    public abstract PortfolioResponseDto toPortfolioResponseDto(Portfolio portfolio);

    @Named("mapOwnerToString")
    protected String mapUserToString(User user) {return user.getUsername();}
}
