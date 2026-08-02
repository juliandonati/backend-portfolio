package com.juliandonati.backendPortafolio.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormalUserRegisterRequestDto {
    @NotBlank(message = "El usuario debe tener un nombre de acceso")
    private String username;
    @NotBlank(message = "El usuario debe tener una contraseña")
    private String unencryptedPassword;

    @NotBlank(message = "El usuario debe tener un nombre público")
    private String displayName;

    @Email(message = "Formato de correo electrónico inválido")
    @NotNull(message = "El usuario debe tener un correo electrónico")
    private String email;

    private String recoveryEmail;
}
