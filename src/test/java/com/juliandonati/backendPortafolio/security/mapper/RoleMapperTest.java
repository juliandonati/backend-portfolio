package com.juliandonati.backendPortafolio.security.mapper;

import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RoleRequestDto;
import com.juliandonati.backendPortafolio.security.dto.RoleResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {
    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    private final String roleName = "NOMBRE ROLE TEST";
    private final String roleDesc = "DESCRIPCION ROLE TEST";

    @Test
    void testMapRoleRequestDtoToRoleEntity() {
        // Arrange
        RoleRequestDto roleRequestDto = new RoleRequestDto();
        roleRequestDto.setName(roleName);
        roleRequestDto.setDescription(roleDesc);

        // Act
        Role result = roleMapper.toEntity(roleRequestDto);

        // Assert
        assertAll("Validando los campos del Role",
                () -> assertNotNull(result),
                () -> assertEquals(roleName, result.getName()),
                () -> assertEquals(roleDesc, result.getDescription())
        );
    }

    @Test
    void testMapRoleEntityToRoleResponseDto() {
        // Arrange
        String username = "pedrito";
        Role role = new Role(null, roleName, roleDesc, Set.of(new User(null, username, null, null, null, Set.of(), null, Set.of())));

        // Act
        RoleResponseDto result = roleMapper.toResponseDto(role);

        // Assert
        assertAll("Validando los campos del RoleResponseDto",
                () -> assertNotNull(result),
                () -> assertEquals(roleName, result.getName()),
                () -> assertEquals(roleDesc, result.getDescription()),
                () -> assertEquals(1, result.getUsers().size()),
                () -> assertTrue(result.getUsers().stream().anyMatch(u -> u.equals(username)))
        );
    }

    @Test
    void testMapUserEntitiesToStrings() {
        // Arrange
        User user1 = new User(), user2 = new User(), user3 = new User();
        String username1 = "pedrito.perez";
        String username2 = "carla.gonzalez";
        String username3 = "lautaro";
        user1.setUsername(username1);
        user2.setUsername(username2);
        user3.setUsername(username3);
        Set<User> userSet = Set.of(user1, user2, user3);

        // Act
        Set<String> result = roleMapper.mapUsersToString(userSet);


        // Assert
        assertAll("Validando los String",
                () -> assertNotNull(result),
                () -> assertTrue(result.stream().anyMatch(u -> u.equals(username1))),
                () -> assertTrue(result.stream().anyMatch(u -> u.equals(username2))),
                () -> assertTrue(result.stream().anyMatch(u -> u.equals(username3)))
        );
    }
}