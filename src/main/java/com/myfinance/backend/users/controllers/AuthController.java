package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.ApiResponse;
import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.ResetPasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;
import com.myfinance.backend.users.services.AuthService;
import com.myfinance.backend.users.services.JwtTokenProvider;

import jakarta.mail.MessagingException;

import com.myfinance.backend.users.services.EmailService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    private final UserRepository userRepository;

    // Login: Recibe la data en json y devuelve un token
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok("Bearer " + token);
    }

    // Registro: Recibe los datos del nuevo usuario y lo crea
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody AppUser appUser) {
        boolean success = authService.register(appUser);
        if (success) {
            return ResponseEntity.ok(new ApiResponse("Registro exitoso"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Error en el registro"));
    }

    // Envia un correo de recuperación de contraseña al correo si existe en la base
    @GetMapping("/password-recovery")
    public String recoverPassword(@RequestParam String email) throws MessagingException, IOException {
        Optional<AppUser> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Generar el token de recuperación
        String token = jwtTokenProvider.createRecoveryToken(email);

        try {
            emailService.sendRecoveryEmail(email, token);
            // Enviar el correo
            return "Correo de recuperación enviado";
        } catch (Exception e) {
            return "Error en el envio del correo de recuperación";
        }

    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        // Validar el token de recuperación
        if (!jwtTokenProvider.validateRecoveryToken(request.getToken())) {
            throw new RuntimeException("Token inválido o expirado");
        }

        // Obtener el email del token
        String email = jwtTokenProvider.getUsernameFromToken(request.getToken());

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        authService.resetPassword(user, request.getNewPassword(), request.getConfirmPassword());

        return "Contraseña actualizada exitosamente";
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
