package com.juliandonati.backendPortafolio.data;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(1)
@Profile("!test")
public class UserDataLoader implements CommandLineRunner {
    private final UserService userService;

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) throws Exception {
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
