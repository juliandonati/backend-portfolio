package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.TEST_THROWS_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleServiceTest {
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final EntityManager entityManager;
    @Autowired
    public RoleServiceTest(RoleService roleService,
                           RoleRepository roleRepository,
                           EntityManager entityManager){
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.entityManager = entityManager;
    }

    private final String roleName1 = "ROLE_USER";
    private final String roleDesc1 = "DESC USER";
    private final String roleName2 = "ROLE_MODERATOR";
    private final String roleDesc2 = "DESC MODERATOR";

    @Test
    void testFindAllRolesReturnsListOfRoles() {
        // Arrange
        Long roleId1 = roleRepository.save(
                new Role(null, roleName1, roleDesc1, Set.of())
        ).getId();
        Long roleId2 = roleRepository.save(
                new Role(null, roleName2, roleDesc2, Set.of())
        ).getId();

        // Act
        List<Role> result = roleService.findAll();
        Role result1 = result.stream().filter(r -> r.getName().equals(roleName1)).findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente el rol 1"));
        Role result2 = result.stream().filter(r -> r.getName().equals(roleName2)).findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente el rol 2"));

        // Assert
        assertAll("Validando los campos de los Roles",
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(roleId1, result1.getId()),
                () -> assertEquals(roleName1, result1.getName()),
                () -> assertEquals(roleDesc1, result1.getDescription()),
                () -> assertEquals(roleId2, result2.getId()),
                () -> assertEquals(roleName2, result2.getName()),
                () -> assertEquals(roleDesc2, result2.getDescription())
        );
    }

    @Test
    void testFindRoleByNameReturnsRoleSuccessfully() {
        // Arrange
        Long roleId = roleRepository.save(
                new Role(null, roleName1, roleDesc1, Set.of())
        ).getId();

        // Act
        Role result = roleService.findByName(roleName1);

        // Assert
        assertAll("Validando los campos del Role",
                () -> assertNotNull(result),
                () -> assertEquals(roleId, result.getId()),
                () -> assertEquals(roleName1, result.getName()),
                () -> assertEquals(roleDesc1, result.getDescription())
        );
    }

    @Test
    void testRoleCRUDLifeCycle() {
        // Arrange
        Role roleToSave = new Role(null, roleName1, roleDesc1, Set.of());

        // CREATE
        Role savedRole = roleService.save(roleToSave);
        Long roleId = savedRole.getId();

        assertAll("Validando los campos del savedRole",
                () -> assertNotNull(savedRole),
                () -> assertNotNull(roleId),
                () -> assertEquals(roleName1, savedRole.getName()),
                () -> assertEquals(roleDesc1, savedRole.getDescription())
        );

        // READ
        // Act
        Role searchedRole = roleService.findById(roleId);

        // Assert
        assertAll("Validando los campos del searchedRole",
                () -> assertNotNull(searchedRole),
                () -> assertEquals(roleId, searchedRole.getId()),
                () -> assertEquals(roleName1, searchedRole.getName()),
                () -> assertEquals(roleDesc1, searchedRole.getDescription())
        );

        // Los roles no tienen update, por lo cual no existe tal parte del test del ciclo de vida CRUD de los roles

        // DELETE
        entityManager.clear();
        assertDoesNotThrow(() -> {
            roleService.deleteById(roleId);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertFalse(roleRepository.existsById(roleId));
    }
}