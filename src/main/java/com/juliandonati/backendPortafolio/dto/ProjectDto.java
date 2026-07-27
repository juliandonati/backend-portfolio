package com.juliandonati.backendPortafolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Debes introducir un título para el proyecto")
    @Size(max = 30, message = "El título no puede superar los 30 caracteres")
    private String title;
    @NotBlank(message = "Debes introducir una descripción para el proyecto")
    @Size(max = 255, message = "La descripción del proyecto no puede superar los 255 caracteres")
    private String description;
    @NotBlank(message = "Debes introducir una fecha de inicio para el proyecto")
    private LocalDate startDate;
    private LocalDate endDate;
    private String url;
    private String imgUrl;
}
