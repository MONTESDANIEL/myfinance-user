package com.myfinance.backend.users.entities.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty("password")
    private String password;

}
