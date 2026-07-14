package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import com.juliandonati.backendPortafolio.exception.DuplicatedAttributeException;
import com.juliandonati.backendPortafolio.mapper.AboutMeMapper;
import com.juliandonati.backendPortafolio.service.AboutMeService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import com.juliandonati.backendPortafolio.service.PresentationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/about-me")

@Tag(name = "About-Me", description = "CRUD del About-Me individual de cada portafolio")

@RequiredArgsConstructor
public class AboutMeController {
    private final AboutMeService aboutMeService;
    private final AboutMeMapper aboutMeMapper;

    private final PortfolioService portfolioService;

    private final Logger logger = LoggerFactory.getLogger(AboutMeController.class);
    private final PresentationService presentationService;

    @GetMapping("/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Consultar About-Me por nombre de dueño de un portafolio",
            description = "Devuelve el About-Me de un portafolio buscándolo por el nombre de su dueño, incluyendo todos sus campos.")
    public ResponseEntity<AboutMeDto> getAboutMeByOwner(@PathVariable String ownerUsername){
        logger.debug("Buscando About-Me de "+ownerUsername);
        AboutMeDto aboutMeDto = aboutMeService.findByOwnerUsername(ownerUsername);
        logger.info("¡Devolviendo About-Me de "+ownerUsername+'!');
        return ResponseEntity.ok(aboutMeDto);
    }

    @PostMapping("/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Publicar About-Me por nombre del dueño de un portafolio",
            description = "Publica un nuevo About-Me con todos sus campos, al portafolio perteneciente al usuario cuyo nombre es especificado")
    public ResponseEntity<AboutMeDto> createAboutMe(@PathVariable String ownerUsername,
                                                    @RequestBody @Valid AboutMeDto aboutMeDto){

        logger.debug("Verificando si "+ownerUsername+" ya tiene un About-me");
        if(!aboutMeService.existsByOwnerUsername(ownerUsername)){
            logger.debug("Recuperando el portafolio de "+ ownerUsername);
            Portfolio portfolio = portfolioService.findByOwnerUsername(ownerUsername);
            AboutMe aboutMe = aboutMeMapper.toEntity(aboutMeDto);

            aboutMe.setPortfolio(portfolio);
            portfolio.setAboutMe(aboutMe);

            logger.debug("Guardando el About-Me de " +  ownerUsername);
            portfolioService.save(portfolio);

            logger.info("¡Se creó el About-Me de "+ownerUsername+'!');
            return new ResponseEntity<>(aboutMeMapper.toDto(aboutMe), HttpStatus.CREATED);
        }
        else
            throw new DuplicatedAttributeException("No puedes tener más de un apartado 'SOBRE MÍ'");
    }

    @PutMapping("/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Actualizar About-Me por nombre del dueño de un portafolio",
            description = "Actualiza los campos modificados de un About-Me especificada por el nombre del dueño del portafolio al que pertenece")
    public ResponseEntity<String> updateAboutMe(@PathVariable String ownerUsername,
                                                    @RequestBody @Valid AboutMeDto aboutMeDto){
        logger.debug("Verificando que exista el About-Me de "+ ownerUsername);
        Long aboutMeId = aboutMeService.findByOwnerUsername(ownerUsername).getId();
        logger.debug("¡El About-Me de"+ownerUsername+" existe! Se recuperó la ID.");
        aboutMeService.update(aboutMeDto, aboutMeId);

        logger.info("¡Se actualizó el About-Me de "+ownerUsername+'!');
        return ResponseEntity.ok("¡'SOBRE MÍ' actualizado con éxito!");
    }

    @DeleteMapping("/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Eliminar About-Me por nombre del dueño de un portafolio",
            description = "Elimina un About-Me, si existe, especificado por el nombre del dueño del portafolio al que pertenece")

    public ResponseEntity<Void> deleteAboutMe(@PathVariable String ownerUsername){
        logger.debug("Eliminando el About-Me de "+ownerUsername);

        // todo Cuando permita subir imágenes, actualizar que aquí se borre.
        logger.debug("Obteniendo id del About-Me...");
        portfolioService.deletePresentationById(presentationService.findByOwnerUsername(ownerUsername).getId());
        logger.info("¡About-Me de "+ownerUsername+" eliminado con éxito!");
        return ResponseEntity.noContent().build();
    }
}
