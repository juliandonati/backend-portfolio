package com.juliandonati.backendPortafolio.security.mapper;

import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.NormalUserRegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    protected RoleService roleService;
    protected PasswordEncoder passwordEncoder;

    @Autowired
    public void setRoleService(RoleService roleService){this.roleService = roleService;}
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){this.passwordEncoder = passwordEncoder;}

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "registerRequestDto.unencryptedPassword", qualifiedByName = "encryptPassword")
    @Mapping(target = "modifiablePortfolios", ignore = true)
    @Mapping(target = "roles", source = "registerRequestDto.roles", qualifiedByName = "mapStringToRoles")
    @Mapping(target = "ownedPortfolio", ignore = true)
    public abstract User toEntity(RegisterRequestDto registerRequestDto);

    @Named("mapStringToRoles")
    protected Set<Role> mapStringToRoles(Set<String> roleStringSet){
        if(roleStringSet == null || roleStringSet.isEmpty())
            return Collections.emptySet(); // Si no tiene roles, directamente no se le asigna ninguno.

        return roleStringSet.stream().map(roleService::findByName).collect(Collectors.toSet());
    }

    @Named("encryptPassword")
    protected String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }


    public abstract UserSummaryResponseDto toUserSummaryResponseDto(User user);

    @Mapping(target = "roles", expression = "java(java.util.Set.of(\"ROLE_USER\"))")
    public abstract RegisterRequestDto toRegisterRequestDto(NormalUserRegisterRequestDto normalUserRegisterRequestDto);
}
