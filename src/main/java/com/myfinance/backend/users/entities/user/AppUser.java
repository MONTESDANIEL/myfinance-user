package com.myfinance.backend.users.entities.user;

import java.sql.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class AppUser {

    @Id
    @NotNull(message = "El número de identificación es obligatorio")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio y no puede estar vacío")
    @NotNull(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El correo es obligatorio y no puede estar vacío")
    @NotNull(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    @Min(value = 1000000000, message = "El número de teléfono debe tener al menos 10 dígitos")
    @Max(value = 9999999999L, message = "El número de teléfono no puede tener más de 10 dígitos")
    @NotNull(message = "El número de teléfono es obligatorio")
    @Column(name = "phone_number")
    private Long phoneNumber;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(name = "birth_date")
    private Date birthDate;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
