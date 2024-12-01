package com.myfinance.backend.users.entities.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordRecovery {

    @Email(message = "El correo electrónico debe ser válido.")
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    @NotBlank(message = "El token de recuperación no puede estar vacío.")
    private String recoveryToken;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe contener al menos una letra mayúscula")
    @Pattern(regexp = ".*[a-z].*", message = "La contraseña debe contener al menos una letra minúscula")
    @Pattern(regexp = ".*\\d.*", message = "La contraseña debe contener al menos un número")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>._-].*", message = "La contraseña debe contener al menos un carácter especial")
    private String newPassword;

    @NotBlank(message = "La contraseña es obligatoria")
    private String confirmPassword;
}
