package com.juliandonati.backendPortafolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    @NotBlank(message="Debes introducir un nombre para la habilidad")
    @Size(max = 20, message = "El nombre de la habilidad no puede sobrepasar los 20 caracteres")
    private String name;
    @NotBlank(message = "Debes introducir una descripción para tu habilidad")
    @Size(message = "La descripción no puede sobrepasar los 255 carácteres")
    private String description;
    @Size(max = 50, message = "La definición del nivel no puede sobrepasar los 50 carácteres")
    private String level;
    private String imgUrl;
    @Size(max = 50, message = "La definición de categoría no puede sobrepasar los 50 carácteres")
    private String category;
}
