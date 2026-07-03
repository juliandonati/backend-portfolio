package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Presentation;
import com.juliandonati.backendPortafolio.dto.PresentationDto;
import com.juliandonati.backendPortafolio.exception.DuplicatedAttributeException;
import com.juliandonati.backendPortafolio.mapper.PresentationMapper;
import com.juliandonati.backendPortafolio.service.FileStorageService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import com.juliandonati.backendPortafolio.service.PresentationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/presentation")

@RequiredArgsConstructor
public class PresentationController {
    private final PresentationMapper presentationMapper;

    private final PortfolioService portfolioService;

    private final PresentationService presentationService;

    private final FileStorageService fileStorageService;

    private final Logger logger = LoggerFactory.getLogger(PresentationController.class);


    @GetMapping("/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<PresentationDto> getPresentationByOwner(@PathVariable String ownerUsername){
        logger.debug("Recuperando la presentación del portafolio de: {}", ownerUsername);
        PresentationDto presentationDto = presentationService.findByOwnerUsername(ownerUsername);
        logger.info("¡Devolviendo la presentación del portafolio de: {}!", ownerUsername);

        return ResponseEntity.ok(presentationDto);
    }

    // authentication.name tiene como valor el subject de nuestro JWT
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @PostMapping(path = "/{ownerUsername}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PresentationDto> createPresentation(@PathVariable String ownerUsername,
                                                              @RequestPart("presentation") @Valid PresentationDto presentationDto,
                                                              @RequestPart(required = false, value = "img-file") MultipartFile imageMPFile) throws IOException {

        logger.debug("Verificando si la presentación de {} existe...", ownerUsername);
        if(!presentationService.existsByOwnerUsername(ownerUsername)){
            logger.debug("La presentación de {} existe, buscando portafolio...", ownerUsername);
            Portfolio portfolio = portfolioService.findByOwnerUsername(ownerUsername);
            Presentation presentation = presentationMapper.toEntity(presentationDto);


            if(imageMPFile != null && !imageMPFile.isEmpty()){
                logger.debug("El usuario subió una nueva imagen, subiendo a Cloudinary...");
                String imgUrl = fileStorageService.uploadImage(imageMPFile, ownerUsername);
                logger.info("¡Nueva imagen del usuario subida con éxito!");
                presentation.setImgUrl(imgUrl);
            }

            presentation.setPortfolio(portfolio);
            portfolio.setPresentation(presentation);

            logger.debug("Guardando la nueva presentación en el portafolio de {}", ownerUsername);
            portfolioService.save(portfolio);
            logger.info("¡Presentación del portafolio de {} creada con éxito!", ownerUsername);
            return new ResponseEntity<>(presentationDto, HttpStatus.CREATED);
        }
        else
            throw new DuplicatedAttributeException("Solo puede haber una presentación por usuario.");
    }

    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @PutMapping(path = "/{ownerUsername}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PresentationDto> updatePresentation(@PathVariable String ownerUsername,
                                                              @RequestPart("presentation") @Valid PresentationDto presentationDto,
                                                              @RequestPart(value = "img-file", required = false) MultipartFile imageMPFile)
    throws Exception{
        logger.debug("Buscando presentación del portafolio de {}", ownerUsername);
        PresentationDto oldPresentationDto = presentationService.findByOwnerUsername(ownerUsername);
        Long presentationId = oldPresentationDto.getId();
        logger.debug("Se encontró la presentación del portafolio de {}, actualizando...", ownerUsername);





        if(imageMPFile != null && !imageMPFile.isEmpty()){
            logger.debug("El usuario subió una nueva imagen, subiendo a Cloudinary...");
            String imgUrl = fileStorageService.uploadImage(imageMPFile, ownerUsername);
            logger.info("¡Nueva imagen del usuario subida con éxito!");

            String oldImgUrl = oldPresentationDto.getImgUrl();
            if(oldImgUrl != null && !oldImgUrl.isEmpty()) {
                logger.debug("Eliminando imagen vieja...");
                fileStorageService.deleteImageByUrl(oldImgUrl);
                logger.debug("¡Imagen vieja eliminada con éxito!");
            }

            presentationDto.setImgUrl(imgUrl);
        }

        presentationService.update(presentationDto, presentationId);
        logger.info("¡Presentación del portafolio de {} actualizada con éxito!", ownerUsername);

        return new ResponseEntity<>(presentationDto, HttpStatus.OK);
    }

    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @DeleteMapping("/{ownerUsername}")
    public ResponseEntity<Void> deletePresentation(@PathVariable String ownerUsername) throws Exception{
        logger.debug("Eliminando presentación de {}", ownerUsername);

        logger.debug("Eliminando imagen de la presentación...");
        fileStorageService.deleteImageByUrl(presentationService.findImgUrlByOwnerUsername(ownerUsername));
        logger.debug("¡Imagen eliminada con éxito!");

        presentationService.deleteByOwnerUsername(ownerUsername);
        logger.info("¡Presentación del portafolio de {} eliminado con éxito!", ownerUsername);

        return ResponseEntity.noContent().build();
    }
}
