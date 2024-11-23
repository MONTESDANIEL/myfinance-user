package com.myfinance.backend.users.entities.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordRecovery {

    @Email(message = "El correo electrónico debe ser válido.")
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    private String email;

    @NotBlank(message = "El token de recuperación no puede estar vacío.")
    private String recoveryToken;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    private String newPassword;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @NotBlank(message = "La confirmación de la contraseña no puede estar vacía.")
    private String confirmPassword;
}
