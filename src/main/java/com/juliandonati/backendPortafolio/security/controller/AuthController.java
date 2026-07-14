package com.juliandonati.backendPortafolio.security.controller;

import com.juliandonati.backendPortafolio.security.dto.JwtResponseDto;
import com.juliandonati.backendPortafolio.security.dto.LoginRequestDto;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.jwt.JwtGenerator;
import com.juliandonati.backendPortafolio.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")

@Tag(name = "Authorization", description = "Permite el registro y el inicio de sesión de usuarios")

@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario mediante RegisterRequestDto",
            description = "Permite registrar a un nuevo usuario enviando un objeto RegisterRequestDto con sus campos completos y válidos")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        logger.debug("Registrando usuario de username: "+ registerRequestDto.getUsername());
        userService.register(registerRequestDto);
        logger.info("¡Usuario de username: "+ registerRequestDto.getUsername()+" creado con éxito!");

        return new ResponseEntity<>("¡El usuario ha sido creado con éxito!", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión de usuario mediante LoginRequestDto",
            description = "Permite iniciar la sesión de un usuario enviando un objeto LoginRequestDto con sus campos completos y válidos, y devuelve un objeto JwtResponseDto que contiene un JWT")
    public ResponseEntity<JwtResponseDto> authenticateUser(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        logger.debug("Autenticando usuario de login: "+ loginRequestDto.getUsernameOrEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsernameOrEmail(), // Por ahora funciona SOLO con username
                        loginRequestDto.getUnencryptedPassword()
                )
        );
        logger.info("¡Usuario de login: "+loginRequestDto.getUsernameOrEmail()+" autenticado con éxito!");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}
