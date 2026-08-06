package com.juliandonati.backendPortafolio.dto;

import com.juliandonati.backendPortafolio.validation.AfterStartDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor

@AfterStartDate
public class JobDto implements DtoWithDates{
    private Long id;
    @NotBlank(message = "El nombre del trabajo no puede estar vacío")
    @Size(max = 50, message = "El nombre del trabajo no puede sobrepasar los 50 carácteres")
    private String name;
    @NotBlank(message = "Debes definir tu posición en el trabajo")
    @Size(max = 50, message = "La posición del trabajo no puede sobrepasar los 50 carácteres")
    private String position;
    @NotBlank(message = "Debes describir el trabajo")
    @Size(max = 255, message = "La descripción del trabajo no puede sobrepasar los 255 carácteres")
    private String description;
    @NotNull(message = "Debes definir la fecha en la que te contrataron")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    private LocalDate startDate;
    @PastOrPresent(message = "La fecha final no puede ser futura")
    private LocalDate endDate;
}
