package com.juliandonati.backendPortafolio.security.controller;

import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")

@RequiredArgsConstructor

@Tag(name = "Administración", description = "Gestión integral de usuarios y recursos del sistema")
public class AdminController {
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario con rol específico mediante RegisterRequestDto",
            description = "Permite registrar a un nuevo usuario con un rol determinado enviando un objeto RegisterRequestDto con sus campos completos y válidos")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        logger.debug("Registrando usuario de username: {}", registerRequestDto.getUsername());
        userService.register(registerRequestDto);
        logger.info("¡Usuario de username: {} creado con éxito!", registerRequestDto.getUsername());

        return new ResponseEntity<>("¡El usuario ha sido creado con éxito!", HttpStatus.CREATED);
    }
}
