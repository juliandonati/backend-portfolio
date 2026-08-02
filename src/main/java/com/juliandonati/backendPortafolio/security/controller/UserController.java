package com.juliandonati.backendPortafolio.security.controller;

import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.service.UserService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")

@Tag(name = "Users", description = "Permite consultar páginas de usuarios filtradas por letras que contengan sus nombres de usuario")

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PortfolioService portfolioService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    @Operation(summary = "Consultar nombres de usuario coincidentes con filtro",
    description = "Devuelve todos los nombres de usuario y su nombre público que coincidan con el filtro 'name' específicado, páginados en un userSummaryResponseDtoPage")
    public ResponseEntity<Page<UserSummaryResponseDto>> getAll(@RequestParam(required = false) String name,
                                               @PageableDefault(page=0, size = 10, sort="username") Pageable pageable) {
        logger.debug("Recuperando usuarios mediante el filtro: {}", name);
        Page<UserSummaryResponseDto> userSummaryResponseDtoPage = userService.findAll(name, pageable);
        logger.info("¡Devolviendo todos los usuarios que contengan el filtro: {}!", name);
        return ResponseEntity.ok(userSummaryResponseDtoPage);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Eliminar usuario por username", description = "Elimina un usuario especificado por su nombre de usuario, junto a su portafolio")
    @PreAuthorize("authentication.name == #username or hasRole('ADMIN')")
    public ResponseEntity<Page<UserSummaryResponseDto>> deleteUserByUsername(@PathVariable String username) throws Exception{
        logger.debug("Buscando usuario para eliminar...");
        User userToDelete = userService.findByUsername(username);

        if(userToDelete.getOwnedPortfolio() != null) {
            logger.debug("Usuario encontrado, eliminando imagenes de su portafolio...");
            portfolioService.deleteAllPortfolioImagesById(userToDelete.getOwnedPortfolio().getId()); // todo Crear QUERY personalizada
            logger.debug("¡Imagenes del portafolio eliminadas con éxito!");
        }
        // El portfolio se termina eliminando gracias a orphanRemoval
        userService.deleteByUsername(username);
        logger.info("¡Usuario eliminado con éxito!");
        return ResponseEntity.noContent().build();
    }
}
