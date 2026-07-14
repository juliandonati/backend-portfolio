package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.SkillMapper;
import com.juliandonati.backendPortafolio.service.FileStorageService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import com.juliandonati.backendPortafolio.service.SkillService;
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

@RestController
@RequestMapping("/api/v1/skills")

@Tag(name = "Skills", description = "CRUD de las habilidades pertenecientes a cada portafolio")

@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillMapper skillMapper;

    private final PortfolioService portfolioService;

    private final FileStorageService fileStorageService;

    private final Logger logger = LoggerFactory.getLogger(SkillController.class);

    @GetMapping("/list/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Consultar habilidad por nombre del dueño de un portafolio",
            description = "Devuelve todas las habilidades de un portafolio buscándolas por el nombre de su dueño, incluyendo todos sus campos.")
    public ResponseEntity<List<SkillDto>> getAllSkillsByOwner(@PathVariable String ownerUsername){
        logger.debug("Buscando habilidades del portafolio de "+ownerUsername);
        List<SkillDto> skillDtos = skillService.findSkillsByOwnerUsername(ownerUsername);
        logger.info("¡Devolviendo habilidades del portafolio de "+ownerUsername+'!');

        return ResponseEntity.ok(skillDtos);
    }

    @PostMapping(path = "/{ownerUsername}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Publicar habilidad por nombre del dueño de un portafolio",
            description = "Publica una nueva habilidad con todos sus campos, al portafolio perteneciente al usuario cuyo nombre es especificado")
    public ResponseEntity<List<SkillDto>> createSkill(@PathVariable String ownerUsername,
                                                      @Valid @RequestPart("skill") SkillDto skillDto,
                                                      @RequestPart(required = false, value = "img-file") MultipartFile imgMpFile)
    throws IOException {
        logger.debug("Buscando portafolio de "+ownerUsername);
        Portfolio portfolio = portfolioService.findByOwnerUsername(ownerUsername);

        logger.debug("Portafolio de "+ownerUsername+" encontrado, añadiendo habilidad...");

        if(imgMpFile != null && !imgMpFile.isEmpty()){
            logger.debug("El usuario agregó una imagen, subiendo...");
            String imgUrl = fileStorageService.uploadImage(imgMpFile,ownerUsername);
            logger.debug("¡Imagen subida con éxito!");
            skillDto.setImgUrl(imgUrl);
        }

        portfolio.addSkill(skillMapper.toEntity(skillDto));
        List<SkillDto> updatedSkillDtos = portfolioService.save(portfolio)
                .getSkills()
                .stream().map(skillMapper::toDto)
                .toList();
        logger.info("¡Habilidad nueva del portafolio de "+ownerUsername+" creada con éxito!");

        return new ResponseEntity<>(updatedSkillDtos, HttpStatus.CREATED);
    }

    @GetMapping("/{skillId}")
    @PreAuthorize("@skillSecurityEvaluator.isOwner(#skillId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Consultar habilidad por ID",
            description = "Devuelve una habilidad específica buscándola por ID, incluyendo todos sus campos.")
    public ResponseEntity<SkillDto> getSkill(@PathVariable Long skillId){
        logger.debug("Buscando habilidad de id: "+skillId);
        SkillDto skillDto = skillService.findById(skillId);
        logger.info("¡Devolviendo habilidad de id: "+skillId+'!');

        return ResponseEntity.ok(skillDto);
    }

    @PutMapping("/{skillId}")
    @PreAuthorize("@skillSecurityEvaluator.isOwner(#skillId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Actualizar habilidad por ID",
            description = "Actualiza los campos modificados de una habilidad especificada por su ID")
    public ResponseEntity<SkillDto> updateSkill(@PathVariable Long skillId,
                                                @Valid @RequestPart("skill") SkillDto skillDto,
                                                @RequestPart(required = false, value = "img-file") MultipartFile imgMpFile)
    throws Exception {

        if(imgMpFile != null && !imgMpFile.isEmpty()){
            logger.debug("El usuario agregó una imagen, subiendo...");
            String imgUrl = fileStorageService.uploadImage(imgMpFile, skillService.findOwnerUsernameBySkillId(skillId));
            logger.debug("¡Imagen subida con éxito!");

            String oldImgUrl = skillService.findImgUrlBySkillId(skillId);
            if(oldImgUrl != null && !oldImgUrl.isEmpty()){
                logger.debug("Eliminando imagen vieja...");
                fileStorageService.deleteImageByUrl(oldImgUrl);
                logger.debug("¡Imagen vieja eliminada con éxito!");
            }

            skillDto.setImgUrl(imgUrl);
        }

        logger.debug("Actualizando habilidad de id: "+skillId+'!');
        SkillDto updatedSkillDto = skillService.update(skillDto, skillId);
        logger.info("¡Habilidad de id: "+skillDto+" actualizada con éxito!");
        return ResponseEntity.ok(updatedSkillDto);
    }

    @DeleteMapping("/{skillId}")
    @PreAuthorize("@skillSecurityEvaluator.isOwner(#skillId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Eliminar habilidad por ID",
            description = "Elimina una habilidad, si existe, especificada por su ID")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long skillId) throws Exception{
        logger.debug("Eliminando habilidad de id: "+skillId+'!');

        try{
            logger.debug("Eliminando imagen de la habilidad...");
            fileStorageService.deleteImageByUrl(skillService.findImgUrlBySkillId(skillId));
            logger.debug("¡Imagen eliminada con éxito!");
        }
        catch(ResourceNotFoundException e){
            logger.debug("La habilidad no tiene una imagen que eliminar.");
        }

        skillService.deleteById(skillId);
        logger.info("¡Habilidad de id: "+skillId+" eliminada con éxito!");

        return ResponseEntity.noContent().build();
    }
}
