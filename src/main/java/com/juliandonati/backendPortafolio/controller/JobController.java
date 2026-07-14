package com.juliandonati.backendPortafolio.controller;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.JobDto;
import com.juliandonati.backendPortafolio.mapper.JobMapper;
import com.juliandonati.backendPortafolio.service.JobService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
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


import java.util.List;

@RestController
@RequestMapping("/api/v1/experience")

@Tag(name="Experience", description = "CRUD de la experiencia laboral perteneciente a cada portafolio")

@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final JobMapper jobMapper;

    private final PortfolioService portfolioService;

    private final Logger logger = LoggerFactory.getLogger(JobController.class);

    @GetMapping("/list/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Consultar experiencia laboral por nombre del dueño de un portafolio",
            description = "Devuelve la experiencia laboral de un portafolio buscándolos por el nombre de su dueño, incluyendo todos los campos de cada trabajo.")
    public ResponseEntity<List<JobDto>> getAllJobsByOwner(@PathVariable String ownerUsername){
        logger.debug("Recuperando la experiencia laboral de "+ownerUsername);
        List<JobDto> jobDtos = jobService.findByOwnerUsername(ownerUsername);
        logger.info("¡Devolviendo la experiencia laboral de "+ownerUsername+'!');

        return ResponseEntity.ok(jobDtos);
    }


    @GetMapping("/{jobId}")
    @PreAuthorize("@jobSecurityEvaluator.isOwner(#jobId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Consultar trabajo por ID",
            description = "Devuelve un trabajo específico buscándolo por ID, incluyendo todos sus campos.")
    public ResponseEntity<JobDto> getJobById(@PathVariable Long jobId){
        logger.debug("Recuperando el trabajo de id: "+jobId);
        JobDto jobDto = jobService.findById(jobId);
        logger.info("¡Devolviendo el trabajo de id: "+jobId+'!');

        return ResponseEntity.ok(jobDto);
    }

    @PostMapping("/{ownerUsername}")
    @PreAuthorize("#ownerUsername == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Publicar trabajo por nombre del dueño de un portafolio",
            description = "Publica un nuevo trabajo con todos sus campos, al portafolio perteneciente al usuario cuyo nombre es especificado")
    public ResponseEntity<List<JobDto>> createJob(@PathVariable String ownerUsername, @Valid @RequestBody JobDto jobDto){
        logger.debug("Recuperando el portafolio de "+ ownerUsername);
        Portfolio portfolio = portfolioService.findByOwnerUsername(ownerUsername);
        portfolio.addExperience(jobMapper.toEntity(jobDto));

        logger.debug("Guardando el portafolio con el nuevo trabajo");
        List<JobDto> updatedExperienceDto = portfolioService.save(portfolio)
                .getExperience()
                .stream().map(jobMapper::toDto).toList();

        logger.info("¡Nuevo trabajo de "+ownerUsername+" creado con éxito!");
        return new ResponseEntity<>(updatedExperienceDto, HttpStatus.CREATED);
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("@jobSecurityEvaluator.isOwner(#jobId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Actualizar trabajo por ID",
            description = "Actualiza los campos modificados de un trabajo especificado por su ID")
    public ResponseEntity<JobDto> updateJob(@PathVariable Long jobId, @Valid @RequestBody JobDto jobDto){
        logger.debug("Recuperando el trabajo de id: "+jobId);
        JobDto updatedJob = jobService.update(jobDto, jobId);
        logger.info("¡Trabajo de id: "+jobId+" actualizado con éxito!");

        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("@jobSecurityEvaluator.isOwner(#jobId,authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Eliminar trabajo por ID",
            description = "Elimina un trabajo, si existe, especificado por su ID")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId){
        logger.debug("Eliminando trabajo de id: "+ jobId);
        jobService.deleteById(jobId);
        logger.info("¡Trabajo de id: "+jobId+" eliminado con éxito!");

        return ResponseEntity.noContent().build();
    }
}
