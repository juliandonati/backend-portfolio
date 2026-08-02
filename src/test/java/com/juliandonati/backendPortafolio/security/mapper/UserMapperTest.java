package com.juliandonati.backendPortafolio.security.mapper;

import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.NormalUserRegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final String username = "usuariotest486";
    private final String dname = "usuariotest486";
    private final String password = "1234";
    private final String email = "testemail@hotmail.com";

    @Test
    void testMapRegisterRequestDtoToUserEntitySuccessfully() {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(username);
        registerRequestDto.setDisplayName(dname);
        registerRequestDto.setUnencryptedPassword(password);
        registerRequestDto.setEmail(email);

        // Act
        User result = userMapper.toEntity(registerRequestDto);

        // Assert
        assertAll("Validando los campos del User...",
                () -> assertNotNull(result),
                () -> assertEquals(username, result.getUsername()),
                () -> assertEquals(dname, result.getDisplayName()),
                () -> assertNotEquals(password, result.getPassword()),
                () -> assertEquals(email, result.getEmail())
        );
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    void testMapStringsToRolesSuccessfully() {
        // Arrange
        Long roleId1 = 1L;
        String roleName1 = "ROLE_USER";
        String roleDesc1 = "DESC USER";
        Long roleId2 = 2L;
        String roleName2 = "ROLE_MODERATOR";
        String roleDesc2 = "DESC MODERATOR";

        Set<String> strings = Set.of(roleName1, roleName2);
        when(roleService.findByName(roleName1)).thenReturn(
                new Role(roleId1, roleName1, roleDesc1, Set.of())
        );
        when(roleService.findByName(roleName2)).thenReturn(
                new Role(roleId2, roleName2, roleDesc2, Set.of())
        );

        // Act
        Set<Role> result = userMapper.mapStringToRoles(strings);
        Role result1 = result.stream().filter(r -> r.getId().equals(roleId1)).findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró el role1 tras el mapeo"));
        Role result2 = result.stream().filter(r -> r.getId().equals(roleId2)).findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró el role2 tras el mapeo"));

        // Assert
        assertAll("Validando los campos de los Roles",
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(roleName1, result1.getName()),
                () -> assertEquals(roleDesc1, result1.getDescription()),
                () -> assertEquals(roleName2, result2.getName()),
                () -> assertEquals(roleDesc2, result2.getDescription())
        );
        verify(roleService, times(1)).findByName(roleName1);
        verify(roleService, times(1)).findByName(roleName2);
    }

    @Test
    void testEncryptPasswordSuccessfully() {
        // Act
        String encryptedPassword = userMapper.encryptPassword(password);

        // Assert
        assertAll("Validando la contraseña encriptada...",
                () -> assertNotNull(encryptedPassword),
                () -> assertNotEquals(password, encryptedPassword)
        );
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    void testMapUserEntityToUserSummaryResponseDtoSuccessfully() {
        // Arrange
        Long userId = 4L;
        User user = new User(userId, username, password, dname, email, Set.of(), null, Set.of());

        // Act
        UserSummaryResponseDto result = userMapper.toUserSummaryResponseDto(user);

        // Assert
        assertAll("Validando los campos del UserSummaryResponseDto...",
                () -> assertNotNull(result),
                () -> assertEquals(username,result.getUsername()),
                () -> assertEquals(dname,result.getDisplayName())
        );
    }

    @Test
    void testMapToRegisterRequestDto() {
        // Arrange
        NormalUserRegisterRequestDto normalUserRegisterRequestDto = new NormalUserRegisterRequestDto();
        normalUserRegisterRequestDto.setUsername(username);
        normalUserRegisterRequestDto.setDisplayName(dname);
        normalUserRegisterRequestDto.setUnencryptedPassword(password);
        normalUserRegisterRequestDto.setEmail(email);

        // Act
        RegisterRequestDto result = userMapper.toRegisterRequestDto(normalUserRegisterRequestDto);

        // Assert
        assertAll("Validando los campos del RegisterRequestDto...",
                () -> assertNotNull(result),
                () -> assertEquals(username, result.getUsername()),
                () -> assertEquals(dname, result.getDisplayName()),
                () -> assertEquals(password, result.getUnencryptedPassword()),
                () -> assertEquals(email, result.getEmail()),
                () -> assertEquals(1, result.getRoles().size()),
                () -> assertTrue(result.getRoles().stream().anyMatch(r->r.equals("ROLE_USER")))
        );
    }
}