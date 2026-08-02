package com.juliandonati.backendPortafolio.data;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Order(0)
@Profile("!test")
public class RoleDataLoader implements CommandLineRunner {
    private final RoleService roleService;
    @Override
    public void run(String... args) throws Exception {
        Role userRole;
        try {
            userRole = roleService.findByName("ROLE_USER");
        } catch (ResourceNotFoundException ex) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Rol genérico de usuario");
            roleService.save(userRole);
        }

        Role adminRole;
        try {
            adminRole = roleService.findByName("ROLE_ADMIN");
        } catch (ResourceNotFoundException ex) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Rol genérico de administrador");
            roleService.save(adminRole);
        }
    }
}
