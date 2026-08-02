package com.juliandonati.backendPortafolio.data;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.service.RoleService;
import com.juliandonati.backendPortafolio.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(1)
public class BasicDataLoader implements CommandLineRunner {
    private final RoleService roleService;
    private final UserService userService;

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;

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

        User adminUser;
        try{
            adminUser = userService.findByUsername("julian.donati");
        }
        catch(ResourceNotFoundException ex){
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setUsername("julian.donati");
            requestDto.setUnencryptedPassword(defaultAdminPassword);
            requestDto.setDisplayName("Julián Donati");
            requestDto.setEmail("juliandonati5@gmail.com");

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_USER");

            requestDto.setRoles(roles);

            adminUser = userService.register(requestDto);
        }
    }
}
