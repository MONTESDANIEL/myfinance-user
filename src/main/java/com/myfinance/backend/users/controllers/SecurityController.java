package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.ApiResponse;
import com.myfinance.backend.users.entities.security.AppUserDetails;
import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.PasswordRecovery;
import com.myfinance.backend.users.entities.security.RegisterRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.services.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final AuthService authService;

    // Login: Recibe la data en json y devuelve un token
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok("Bearer " + token);
    }

    @GetMapping("/user")
    public ResponseEntity<AppUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autenticado");
        }

        // Obtiene los detalles del usuario autenticado
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        AppUser appUser = userDetails.getAppUser(); // Aquí está el correo electrónico

        return ResponseEntity.ok(appUser); // Devuelve el usuario encontrado
    }

    // Registro: Recibe los datos del nuevo usuario y lo crea
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
        boolean success = authService.register(registerRequest);
        if (success) {
            return ResponseEntity.ok(new ApiResponse("Registro exitoso"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Error en el registro"));
    }

    // Envia un correo de recuperación de contraseña al correo si existe en la base
    @PostMapping("/password-recovery")
    public ResponseEntity<ApiResponse> recoverPassword(@RequestBody PasswordRecovery recoveryRequest) {
        boolean success = authService.recoverPassword(recoveryRequest);
        if (success) {
            return ResponseEntity.ok(new ApiResponse("Enlace de recuperación enviado"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Error en el proceso de recuperación"));
    }

    // Salir y borrar o inhabilitar token de acceso
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        authService.logout();
        return ResponseEntity.ok(new ApiResponse("Cierre de sesión exitoso"));
    }
}
