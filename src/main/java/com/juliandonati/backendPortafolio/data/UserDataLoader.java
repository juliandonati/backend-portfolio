package com.juliandonati.backendPortafolio.data;

import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import com.juliandonati.backendPortafolio.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final UserRepository userRepository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserDataLoader.class);

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.existsByUsername("julian.donati"))
            logger.debug("EL ADMIN DEFAULT YA EXISTE");
        else{
            logger.debug("EL ADMIN DEFAULT NO EXISTE, CREANDO...");
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setUsername("julian.donati");
            requestDto.setUnencryptedPassword(defaultAdminPassword);
            requestDto.setDisplayName("Julián Donati");
            requestDto.setEmail("juliandonati5@gmail.com");

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_USER");

            requestDto.setRoles(roles);

            userService.register(requestDto);
            logger.info("¡ADMIN DEFAULT CREADO!");
        }
    }
}
