package com.myfinance.backend.users.entities.user;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Data
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Si trabajas con roles, puedes devolver una lista de ellos. Aquí se retorna
        // vacío.
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.email; // Usaremos el correo como nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Personalízalo si usas lógica para expiración de cuentas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Personalízalo si usas lógica para bloqueo de cuentas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Personalízalo si usas lógica para expiración de credenciales
    }

    @Override
    public boolean isEnabled() {
        return true; // Personalízalo si controlas usuarios inactivos
    }
}
