package com.juliandonati.backendPortafolio.security.controller;

import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")

@Tag(name = "Users", description = "Permite consultar páginas de usuarios filtradas por letras que contengan sus nombres de usuario")

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    @Operation(summary = "Consultar nombres de usuario coincidentes con filtro",
    description = "Devuelve todos los nombres de usuario y su nombre público que coincidan con el filtro 'name' específicado, páginados en un userSummaryResponseDtoPage")
    public ResponseEntity<Page<UserSummaryResponseDto>> getAll(@RequestParam(required = false) String name,
                                               @PageableDefault(page=0, size = 10, sort="username") Pageable pageable) {
        logger.debug("Recuperando usuarios mediante el filtro: "+name);
        Page<UserSummaryResponseDto> userSummaryResponseDtoPage = userService.findAll(name, pageable);
        logger.info("¡Devolviendo todos los usuarios que contengan el filtro: "+ name + '!');
        return ResponseEntity.ok(userSummaryResponseDtoPage);
    }
}
