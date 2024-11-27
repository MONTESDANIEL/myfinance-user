package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.ApiResponse;
import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.PasswordRecovery;
import com.myfinance.backend.users.entities.security.RegisterRequest;
import com.myfinance.backend.users.services.AuthService;
import com.myfinance.backend.users.services.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Login: Recibe la data en json y devuelve un token
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok("Bearer " + token);
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
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // Verificar si el encabezado Authorization está presente y en formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(new ApiResponse("Authorization header missing or incorrect"),
                    HttpStatus.BAD_REQUEST);
        }

        // Extraer el token del encabezado
        String token = authorizationHeader.substring(7); // Eliminar "Bearer "

        // Validar el token
        if (!jwtTokenProvider.validateToken(token)) {
            return new ResponseEntity<>(new ApiResponse("Invalid token"), HttpStatus.UNAUTHORIZED);
        }

        // Revocar el token, es decir, agregarlo a la lista de tokens revocados
        jwtTokenProvider.revokeToken(token);

        // Responder indicando que el logout fue exitoso
        return new ResponseEntity<>(new ApiResponse("Logout successful"), HttpStatus.OK);
    }
}
