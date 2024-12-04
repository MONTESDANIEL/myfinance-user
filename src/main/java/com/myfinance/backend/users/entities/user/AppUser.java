package com.myfinance.backend.users.entities.user;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class AppUser {

    @Id
    @Min(value = 10000, message = "El número de identificación no es valido")
    @NotNull(message = "El número de identificación es obligatorio")
    private Long id;

    @NotBlank(message = "El tipo de identificación no puede estar vacío")
    @NotNull(message = "El tipo de identificación es obligatorio")
    @Column(name = "id_type")
    private String idType;

    @NotBlank(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El correo no puede estar vacío")
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
    private LocalDate birthDate;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe contener al menos una letra mayúscula")
    @Pattern(regexp = ".*[a-z].*", message = "La contraseña debe contener al menos una letra minúscula")
    @Pattern(regexp = ".*\\d.*", message = "La contraseña debe contener al menos un número")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>._-].*", message = "La contraseña debe contener al menos un carácter especial")
    private String password;
}
