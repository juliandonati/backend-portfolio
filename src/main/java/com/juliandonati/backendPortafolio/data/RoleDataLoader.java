package com.juliandonati.backendPortafolio.data;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Order(0)
@Profile("!test")
public class RoleDataLoader implements CommandLineRunner {
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(RoleDataLoader.class);
    @Override
    public void run(String... args) throws Exception {
        Role userRole;
        try {
            userRole = roleService.findByName("ROLE_USER");
            logger.debug("EL ROL DE USER YA EXISTE");
        } catch (ResourceNotFoundException ex) {
            logger.debug("CREANDO ROL DE USER");
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Rol genérico de usuario");
            roleService.save(userRole);
            logger.info("¡ROL DE USER CREADO CON ÉXITO!");
        }

        Role adminRole;
        try {
            adminRole = roleService.findByName("ROLE_ADMIN");
            logger.debug("EL ROL DE USER YA EXISTE");
        } catch (ResourceNotFoundException ex) {
            logger.debug("CREANDO ROL DE ADMIN");
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Rol genérico de administrador");
            roleService.save(adminRole);
            logger.info("¡ROL DE ADMIN CREADO CON ÉXITO!");
        }
    }
}
