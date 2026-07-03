package com.juliandonati.backendPortafolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DegreeDto {
    private Long id;
    @NotBlank(message = "Debes ingresar el nombre del título académico")
    @Size(max=50, message="El nombre del título no puede superar los 50 carácteres")
    private String name;
    @Size(max=255, message="La descripción del título no puede superar los 255 carácteres")
    private String description;
    @NotNull(message = "Debes ingresar la fecha en la que comenzaste el título académico")
    private LocalDate startDate;
    private LocalDate endDate;
    private String imgUrl;
}
