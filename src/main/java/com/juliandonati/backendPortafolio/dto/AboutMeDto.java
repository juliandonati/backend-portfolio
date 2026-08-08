package com.juliandonati.backendPortafolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutMeDto {
    private Long id;
    @NotBlank(message = "El título de tu 'SOBRE MÍ' no puede estar en blanco")
    @Size(max = 50, message = "El título no puede sobrepasar los 50 carácteres")
    private String title;
    @NotBlank(message = "Debes ingresar una descripción")
    @Size(max=255, message = "La descripción no puede sobrepasar los 255 carácteres")
    private String description;
    @URL(message = "El formato de la URL es incorrecto")
    private String bgImgUrl;
    private String buttonText;
    @URL(message = "El formato de la URL es incorrecto")
    private String buttonUrl;
}
