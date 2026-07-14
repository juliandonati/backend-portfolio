package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.Degree;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Skill;
import com.juliandonati.backendPortafolio.dto.PortfolioResponseDto;
import com.juliandonati.backendPortafolio.exception.DuplicatedAttributeException;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.PortfolioMapper;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.service.UserService;
import com.juliandonati.backendPortafolio.service.FileStorageService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/v1/portfolio")

@Tag(name = "Portfolio", description = "CRUD para modificar cada portfolio individualmente")

@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioMapper portfolioMapper;

    private final UserService userService;
    private final FileStorageService fileStorageService;

    private final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    // TODOS, incluso quienes no están logueados pueden acceder a este metodo
    @GetMapping("/{ownerUsername}")
    @Operation(summary = "Consultar portafolio por nombre del dueño",
            description = "Devuelve un portafolio específico buscándolo por el nombre de su dueño, incluyendo todos sus campos")
    public ResponseEntity<PortfolioResponseDto> getPortfolioByOwner(@PathVariable String ownerUsername){
        logger.debug("Recuperando el portafolio de: {}", ownerUsername);
        PortfolioResponseDto portfolioResponseDto = portfolioMapper.toPortfolioResponseDto(portfolioService.findByOwnerUsername(ownerUsername));
        logger.info("¡Portafolio de {} recuperado con éxito!", ownerUsername);

        return ResponseEntity.ok(portfolioResponseDto);
    }

    @PreAuthorize("authentication.name == #ownerUsername or hasRole('ADMIN')")
    @PostMapping("/{ownerUsername}")
    @Operation(summary = "Publicar portafolio por nombre de usuario",
            description = "Publica un nuevo portafolio con todos sus campos, que tiene como dueño el usuario cuyo nombre fue específicado en la ruta")
    public ResponseEntity<PortfolioResponseDto> createPortfolio(@PathVariable String ownerUsername){
        logger.debug("Verificando si existe el portafolio de {}", ownerUsername);
        if(!portfolioService.existsByOwnerUsername(ownerUsername)){
                logger.debug("El portafolio de {} no existe, buscando usuario...", ownerUsername);
                User user = userService.findByUsername(ownerUsername);
                logger.debug("El usuario de {} existe, creando portafolio...", ownerUsername);
                Portfolio newPortfolio = new Portfolio();
                newPortfolio.setOwner(user);
                user.setOwnedPortfolio(newPortfolio);

                userService.save(user); // Efecto cascada. portfolioRepository automáticamente guardará a newPortfolio y se actualizará la JoinTable.
            logger.info("¡Portafolio de {} creado con éxito!", ownerUsername);
                return new ResponseEntity<>(portfolioMapper.toPortfolioResponseDto(newPortfolio), HttpStatus.CREATED);
        }
        else
            throw new DuplicatedAttributeException("No puede haber más de un portafolio por usuario.");
    }

    @PreAuthorize("authentication.name == #ownerUsername or hasRole('ADMIN')")
    @DeleteMapping("/{ownerUsername}")
    @Operation(summary = "Eliminar portafolio por nombre del dueño",
            description = "Elimina el portafolio, si existe, perteneciente a su dueño, cuyo nombre es específicado en la ruta")
    public ResponseEntity<String> deletePortfolio(@PathVariable String ownerUsername) throws Exception{
        logger.debug("Verificando si existe el usuario de {}", ownerUsername);
        User user = userService.findByUsername(ownerUsername);

        logger.debug("Verificando si {} tiene un portafolio", ownerUsername);
        Portfolio portfolio = user.getOwnedPortfolio();
        if(portfolio == null)
            throw new ResourceNotFoundException("El usuario no tiene un portfolio");

        logger.debug("{} tiene un portafolio, eliminando...", ownerUsername);

        logger.debug("Eliminando todas las imágenes del portafolio...");
        List<String> imgUrlList = new ArrayList<>(portfolio.getSkills().stream().map(Skill::getImgUrl).toList());
        imgUrlList.add(portfolio.getPresentation().getImgUrl());
        imgUrlList.addAll(portfolio.getDegrees().stream().map(Degree::getImgUrl).toList());

        for(String imgUrl : imgUrlList)
            fileStorageService.deleteImageByUrl(imgUrl);
        logger.debug("¡Imágenes eliminadas con éxito!");

        user.setOwnedPortfolio(null); // orphanRemoval elimina automáticamente el portfolio
        userService.save(user);

        logger.debug("Se guardo el usuario ahora sin portafolio");
        logger.info("¡Portafolio de {} eliminado con éxito!", ownerUsername);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{ownerUsername}/exists")
    @Operation(summary = "Consultar si un portafolio existe por nombre de usuario",
            description = "Devuelve un valor Boolean = Verdadero si el usuario cuyo nombre fue especificado en la ruta posee un portafolio existente")
    public ResponseEntity<Boolean> existsPortfolio(@PathVariable String ownerUsername){
        try{
            logger.debug("Verificando si existe un portafolio de dueño: {}", ownerUsername);
            boolean exists = portfolioService.existsByOwnerUsername(ownerUsername);
            logger.info(exists ? "¡Existe!": "No existe." );
            return ResponseEntity.ok(exists);
        }
        catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
