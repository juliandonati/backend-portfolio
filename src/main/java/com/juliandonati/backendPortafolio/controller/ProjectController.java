package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.mapper.ProjectMapper;
import com.juliandonati.backendPortafolio.service.FileStorageService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import com.juliandonati.backendPortafolio.service.ProjectService;
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
@RequestMapping("/api/v1/projects")

@Tag(name = "Projects", description = "CRUD de la lista de proyectos perteneciente a cada portafolio")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    private final PortfolioService portfolioService;

    private final FileStorageService fileStorageService;

    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @GetMapping("/list/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Consultar proyectos por nombre del dueño de un portafolio",
            description = "Devuelve los proyectos de un portafolio buscándolos por el nombre de su dueño, incluyendo todos los campos de cada proyecto.")
    public ResponseEntity<List<ProjectDto>> getAllProjectsByOwner(@PathVariable String ownerUsername){
        logger.debug("Recuperando los proyectos desarrollados por {}",ownerUsername);
        List<ProjectDto> projectDtos = projectService.findProjectsByOwnerUsername(ownerUsername);
        logger.info("Devolviendo los proyectos desarrollados por {}",ownerUsername);

        return ResponseEntity.ok(projectDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@projectSecurityEvaluator.isOwner(#id,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Consultar proyecto por ID",
    description = "Devuelve un proyecto buscándolo por su ID, incluyendo todos sus campos.")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id){
        logger.debug("Recuperando el proyecto cuya id es {}",id);
        ProjectDto projectDto = projectService.findById(id);
        logger.info("Devolviendo el proyecto cuya id es {}",id);

        return ResponseEntity.ok(projectDto);
    }

    @PostMapping(value = "/{ownerUsername}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Publicar proyecto por nombre del dueño de un portafolio",
            description = "Publica un nuevo proyecto con todos sus campos, al portafolio perteneciente al usuario cuyo nombre es especificado")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<List<ProjectDto>> createProject(@Valid @RequestPart("project") ProjectDto projectDto,
                                                          @RequestPart(required = false, value = "img-file") MultipartFile imgMpFile,
                                                          @PathVariable String ownerUsername)
    throws IOException {
        logger.debug("Buscando portafolio de {}...",ownerUsername);
        Portfolio ownerPortfolio = portfolioService.findByOwnerUsername(ownerUsername);
        logger.debug("¡Portafolio encontrado! Agregando nuevo proyecto...");

        if(imgMpFile != null && !imgMpFile.isEmpty()){
            logger.debug("Subiendo imagen del proyecto...");
            String imgUrl =fileStorageService.uploadImage(imgMpFile,ownerUsername);
            logger.info("¡Imagen del proyecto subida con éxito!");
            projectDto.setImgUrl(imgUrl);
        }

        ownerPortfolio.addProject(
                projectMapper.toEntity(projectDto)
        );
        List<ProjectDto> savedProjectDtos = portfolioService.save(ownerPortfolio).getProjects().stream().map(projectMapper::toDto).toList();
        logger.info("¡Proyecto guardado con éxito!");

        return new ResponseEntity<>(savedProjectDtos, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@projectSecurityEvaluator.isOwner(#id,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Actualizar proyecto por ID",
            description = "Actualiza los campos modificados de un proyecto especificado por su ID")
    public ResponseEntity<ProjectDto> updateProject(@Valid @RequestPart("project") ProjectDto projectDto,
                                                    @RequestPart(required = false, value = "img-file") MultipartFile imgMpFile,
                                                    @PathVariable Long id)
    throws Exception{
        if(imgMpFile != null && !imgMpFile.isEmpty()){
            logger.debug("Nueva imagen detectada, subiendola a la nube...");
            String newImgUrl = fileStorageService.uploadImage(imgMpFile,projectService.findOwnerUsernameByProjectId(id));
            logger.info("¡Nueva imagen del proyecto subida con éxito!");

            String oldImgUrl = projectService.findImgUrlByProjectId(id);
            if(oldImgUrl != null && !oldImgUrl.isEmpty()){
                logger.debug("Eliminando imagen vieja del proyecto...");
                fileStorageService.deleteImageByUrl(oldImgUrl);
                logger.info("¡Imagen vieja del proyecto eliminada con éxito!");
            }
            projectDto.setImgUrl(newImgUrl);
        }
        logger.debug("Actualizando proyecto de id: {}",id);
        ProjectDto updatedProjectDto = projectService.update(projectDto,id);
        logger.info("¡Proyecto actualizado con éxito!");

        return ResponseEntity.ok(updatedProjectDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@projectSecurityEvaluator.isOwner(#id,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Eliminar proyecto por ID",
            description = "Elimina un proyecto, si existe, especificado por su ID")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) throws Exception{
        logger.debug("Eliminando el proyecto de id: {}",id);
        String imgUrl = projectService.findImgUrlByProjectId(id);
        if(imgUrl != null && !imgUrl.isEmpty()){
            logger.debug("El proyecto contenía una imagen, eliminandola...");
            fileStorageService.deleteImageByUrl(imgUrl);
            logger.info("¡Imagen del proyecto eliminada con éxito!");
        }

        projectService.deleteById(id);
        logger.info("¡Proyecto eliminado con éxito!");

        return ResponseEntity.noContent().build();
    }
}
