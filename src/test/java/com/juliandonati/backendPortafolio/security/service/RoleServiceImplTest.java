package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.TEST_THROWS_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private final Long roleId1 = 1L;
    private final String roleName1 = "ROLE_USER";
    private final String roleDesc1 = "DESC USER";
    private final Long roleId2 = 2L;
    private final String roleName2 = "ROLE_MODERATOR";
    private final String roleDesc2 = "DESC MODERATOR";

    @Test
    void testFindAllRolesReturnsListOfRoles() {
        // Arrange
        List<Role> mockRoles = List.of(
                new Role(roleId1, roleName1, roleDesc1, Set.of()),
                new Role(roleId2, roleName2, roleDesc2, Set.of())
        );
        when(roleRepository.findAll()).thenReturn(mockRoles);

        // Act
        List<Role> result = roleService.findAll();

        // Assert
        assertAll("Validando los campos de los Roles",
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(roleId1, result.getFirst().getId()),
                () -> assertEquals(roleName1, result.getFirst().getName()),
                () -> assertEquals(roleDesc1, result.getFirst().getDescription()),
                () -> assertEquals(roleId2, result.get(1).getId()),
                () -> assertEquals(roleName2, result.get(1).getName()),
                () -> assertEquals(roleDesc2, result.get(1).getDescription())
        );
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testFindRoleByIdReturnsRoleSuccessfully() {
        // Arrange
        Role mockRole = new Role(roleId1, roleName1, roleDesc1, Set.of());
        when(roleRepository.findById(roleId1)).thenReturn(Optional.of(mockRole));

        // Act
        Role result = roleService.findById(roleId1);

        // Assert
        assertAll("Validando los campos del Role",
                () -> assertEquals(roleId1, result.getId()),
                () -> assertEquals(roleName1, result.getName()),
                () -> assertEquals(roleDesc1, result.getDescription())
        );
        verify(roleRepository, times(1)).findById(roleId1);
    }

    @Test
    void testFindRoleByIdThrowsResourceNotFoundException() {
        // Arrange
        Long mockId = 99L;
        when(roleRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.findById(mockId));
        verify(roleRepository, times(1)).findById(mockId);
    }

    @Test
    void testFindRoleByNameReturnsRoleSuccessfully() {
        // Arrange
        Role mockRole = new Role(roleId2, roleName2, roleDesc2, Set.of());
        when(roleRepository.findByName(roleName2)).thenReturn(Optional.of(mockRole));

        // Act
        Role result = roleService.findByName(roleName2);

        // Assert
        assertAll("Validando los campos del Role",
                () -> assertNotNull(result),
                () -> assertEquals(roleId2,result.getId()),
                () -> assertEquals(roleName2,result.getName()),
                () -> assertEquals(roleDesc2,result.getDescription())
        );
        verify(roleRepository,times(1)).findByName(roleName2);
    }

    @Test
    void testFindRoleByNameThrowsResourceNotFoundException() {
        // Arrange
        String mockName = "ROLE_UNEXISTENT";
        when(roleRepository.findByName(mockName)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.findByName(mockName));
        verify(roleRepository, times(1)).findByName(mockName);
    }

    @Test
    void testSaveRoleSavesRoleSuccessfully() {
        // Arrange
        Role mockRoleToSave = new Role(null,roleName2,roleDesc2,Set.of());
        when(roleRepository.save(mockRoleToSave)).thenReturn(
                new Role(roleId2,roleName2,roleDesc2,Set.of())
        );

        // Act
        Role result = roleService.save(mockRoleToSave);

        // Assert
        assertAll("Validando los campos del Role",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getId()),
                () -> assertEquals(roleName2,result.getName()),
                () -> assertEquals(roleDesc2,result.getDescription())
        );
        verify(roleRepository,times(1)).save(mockRoleToSave);
    }

    @Test
    void testDeleteRoleByIdDeletesRoleSuccessfully() {
        // Arrange
        when(roleRepository.existsById(roleId2)).thenReturn(true);

        // Act + Assert
        assertDoesNotThrow(()->roleService.deleteById(roleId2),TEST_THROWS_MESSAGE);
        verify(roleRepository,times(1)).existsById(roleId2);
        verify(roleRepository,times(1)).deleteById(roleId2);
    }

    @Test
    void testDeleteRoleByIdThrowsResourceNotFoundException() {
        // Arrange
        Long mockUnexistentId = 99L;
        when(roleRepository.existsById(mockUnexistentId)).thenReturn(false);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->roleService.deleteById(mockUnexistentId));
        verify(roleRepository,times(1)).existsById(mockUnexistentId);
        verify(roleRepository,never()).deleteById(anyLong());
    }
}