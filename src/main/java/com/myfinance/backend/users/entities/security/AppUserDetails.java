package com.myfinance.backend.users.entities.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.myfinance.backend.users.entities.user.AppUser;

public class AppUserDetails implements UserDetails {

    private final AppUser appUser;

    public AppUserDetails(AppUser appUser) {
        this.appUser = appUser;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Si no tienes roles, devuelves una colección vacía
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // Devuelve la contraseña almacenada en la entidad AppUser
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        // Devuelve el correo como nombre de usuario
        return appUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Si no manejas expiración, retorna true
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Si no manejas bloqueo de cuentas, retorna true
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Si no manejas expiración de credenciales, retorna true
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Si no manejas estados deshabilitados, retorna true
        return true;
    }
}
