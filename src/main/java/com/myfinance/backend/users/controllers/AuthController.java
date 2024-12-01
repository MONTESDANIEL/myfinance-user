package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.ResetPasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    // Login: Recibe la data en json y devuelve un token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        ResponseEntity<?> response = authService.login(loginRequest);
        return response;
    }

    // Registro: Recibe los datos del nuevo usuario y lo crea
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AppUser appUser) {
        ResponseEntity<?> response = authService.register(appUser);
        return response;
    }

    // Recuperar contraseña: Envia un correo de recuperación de contraseña al correo
    // si existe en la base
    @GetMapping("/password-recovery")
    public ResponseEntity<?> recoverPassword(@RequestParam String email) {
        ResponseEntity<?> response = authService.recoverPassword(email);
        return response;
    }

    // Restablecer contraseña: Cambio de contraseña con el tokend de la recuperación
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResponseEntity<?> response = authService.resetPassword(request);
        return response;
    }

    // Salir y borrar o inhabilitar token de acceso
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        ResponseEntity<?> response = authService.logout(authorizationHeader);
        return response;
    }

}
