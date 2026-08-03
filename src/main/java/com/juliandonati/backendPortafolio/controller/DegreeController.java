package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.Degree;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.DegreeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.DegreeMapper;
import com.juliandonati.backendPortafolio.service.DegreeService;
import com.juliandonati.backendPortafolio.service.FileStorageService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/degrees")

@Tag(name = "Degrees", description = "CRUD de los títulos académicos pertenecientes a cada portafolio")

@RequiredArgsConstructor
public class DegreeController {
    private final DegreeService degreeService;
    private final PortfolioService portfolioService;

    private final DegreeMapper degreeMapper;

    private final FileStorageService fileStorageService;

    private final Logger logger = LoggerFactory.getLogger(DegreeController.class);


    @GetMapping("/list/{ownerUsername}")
    @PreAuthorize("authentication.name == #ownerUsername or hasRole('ADMIN')")
    @Operation(summary = "Consultar títulos académicos por nombre del dueño de un portafolio",
            description = "Devuelve todos los títulos académicos de un portafolio buscándolos por el nombre de su dueño, incluyendo todos sus campos.")
    public ResponseEntity<List<DegreeDto>> getAllDegreesByOwner(@PathVariable String ownerUsername) {
        logger.debug("Recuperando los títulos académicos de {}", ownerUsername);
        List<DegreeDto> degreeDtos = degreeService.findByOwnerUsername(ownerUsername);
        logger.info("¡Devolviendo los títulos académicos de {}!", ownerUsername);
        return ResponseEntity.ok(degreeDtos);
    }

    @PostMapping(path = "/{ownerUsername}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("authentication.name == #ownerUsername or hasRole('ADMIN')")
    @Operation(summary = "Publicar título académico por nombre del dueño de un portafolio",
            description = "Publica un nuevo título académico con todos sus campos, al portafolio perteneciente al usuario cuyo nombre es especificado")
    public ResponseEntity<List<DegreeDto>> createDegree(@PathVariable String ownerUsername,
                                                        @Valid @RequestPart("degree") DegreeDto degreeDto,
                                                        @Valid @RequestPart(required = false, value = "img-file") MultipartFile imageMpFile)
            throws IOException {
        logger.debug("Buscando portafolio de {}", ownerUsername);
        Portfolio portfolio = portfolioService.findByOwnerUsername(ownerUsername);
        Degree degree = degreeMapper.toEntity(degreeDto);

        if (imageMpFile != null && !imageMpFile.isEmpty()) {
            logger.debug("El usuario subió una imagen, subiendo...");
            String imageUrl = fileStorageService.uploadImage(imageMpFile, ownerUsername);
            logger.debug("¡Imagen subida con éxito!");
            degree.setImgUrl(imageUrl);
        }

        portfolio.addDegree(degree);

        logger.debug("Guardando el nuevo título académico en el portafolio de {}", ownerUsername);
        List<DegreeDto> updatedDegreeList = portfolioService.save(portfolio)
                .getDegrees()
                .stream().map(degreeMapper::toDto).toList();

        logger.info("¡El nuevo título académico de {} ha sido creado correctamente!", ownerUsername);
        return new ResponseEntity<>(updatedDegreeList, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{degreeId}")
    @PreAuthorize("@degreeSecurityEvaluator.isOwner(#degreeId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Consultar título académico por su ID",
            description = "Devuelve un título académico buscándolo por su ID, incluyendo todos sus campos.")
    public ResponseEntity<DegreeDto> getDegreeById(@PathVariable Long degreeId) {
        logger.debug("Recuperando título académico de id: {}", degreeId);
        DegreeDto degreeDto = degreeService.findById(degreeId);
        logger.info("¡Devolviendo el título académico de id: {}!", degreeId);
        return ResponseEntity.ok(degreeDto);
    }

    @PutMapping(path = "/{degreeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@degreeSecurityEvaluator.isOwner(#degreeId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Actualizar título académico por ID",
            description = "Actualiza los campos modificados de un título académico especificado por su ID")
    public ResponseEntity<DegreeDto> updateDegree(@PathVariable Long degreeId,
                                                  @Valid @RequestPart("degree") DegreeDto degreeDto,
                                                  @RequestPart(required = false, value = "img-file") MultipartFile imageMpFile)
            throws Exception {
        logger.debug("Actualizando título académico de id: {}", degreeId);

        if (imageMpFile != null && !imageMpFile.isEmpty()) {
            logger.debug("El usuario subió una nueva imagen, subiendo...");
            String imageUrl = fileStorageService.uploadImage(imageMpFile, degreeService.findOwnerUsernameByDegreeId(degreeId));
            logger.debug("¡Imagen subida con éxito!");

            Optional<String> oldImageUrlOptional = degreeService.findOptionalImgUrlByDegreeId(degreeId);
            if (oldImageUrlOptional.isPresent()) {
                String oldImageUrl = oldImageUrlOptional.get();
                if (!oldImageUrl.trim().isEmpty()) {
                    logger.debug("Eliminando imagen vieja de título académico...");
                    fileStorageService.deleteImageByUrl(oldImageUrl);
                    logger.debug("¡Imagen vieja de título académico eliminada con éxito!");
                }
            }


            degreeDto.setImgUrl(imageUrl);
        }

        DegreeDto updatedDegree = degreeService.update(degreeDto, degreeId);
        logger.info("¡Título académico de id: {} actualizado con éxito!", degreeId);

        return new ResponseEntity<>(updatedDegree, HttpStatus.OK);
    }

    @DeleteMapping("/{degreeId}")
    @PreAuthorize("@degreeSecurityEvaluator.isOwner(#degreeId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Eliminar título académico por ID",
            description = "Elimina un título académico, si existe, especificado por su ID")
    public ResponseEntity<Void> deleteDegree(@PathVariable Long degreeId) throws Exception {
        logger.debug("Eliminando título académico de id: {}", degreeId);

        Optional<String> oldImageUrlOptional = degreeService.findOptionalImgUrlByDegreeId(degreeId);
        if (oldImageUrlOptional.isPresent()) {
            String oldImageUrl = oldImageUrlOptional.get();
            if (!oldImageUrl.trim().isEmpty()) {
                logger.debug("Eliminando imagen de título académico...");
                fileStorageService.deleteImageByUrl(oldImageUrl);
                logger.debug("¡Imagen de título académico eliminada con éxito!");
            }
        }


        degreeService.deleteById(degreeId);
        logger.info("¡Título académico de id: {} eliminado con éxito!", degreeId);

        return ResponseEntity.noContent().build();
    }
}
