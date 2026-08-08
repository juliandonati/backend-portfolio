package com.juliandonati.backendPortafolio.dto;

import com.juliandonati.backendPortafolio.validation.AfterStartDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

@AfterStartDate
public class ProjectDto implements DtoWithDates{
    private Long id;

    @NotBlank(message = "Debes introducir un título para el proyecto")
    @Size(max = 30, message = "El título no puede superar los 30 caracteres")
    private String title;
    @NotBlank(message = "Debes introducir una descripción para el proyecto")
    @Size(max = 255, message = "La descripción del proyecto no puede superar los 255 caracteres")
    private String description;
    @NotNull(message = "Debes introducir una fecha de inicio para el proyecto")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    private LocalDate startDate;
    @PastOrPresent(message = "La fecha de finalización no puede ser futura")
    private LocalDate endDate;
    @URL(message = "El formato de la URL es incorrecto")
    private String url;
    @URL(message = "El formato de la URL es incorrecto")
    private String imgUrl;
}
