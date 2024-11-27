package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.ApiResponse;
import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.RegisterRequest;
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
public class SecurityController {

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
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
        boolean success = authService.register(registerRequest);
        if (success) {
            return ResponseEntity.ok(new ApiResponse("Registro exitoso"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Error en el registro"));
    }

    // Envia un correo de recuperación de contraseña al correo si existe en la base
    @PostMapping("/password-recovery")
    public String recoverPassword(@RequestParam String email) throws MessagingException, IOException {
        Optional<AppUser> user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Generar el token de recuperación
        String token = jwtTokenProvider.createRecoveryToken(email);

        // Enviar el correo
        emailService.sendRecoveryEmail(email, token);

        return "Correo de recuperación enviado";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        // Validar el token de recuperación
        if (!jwtTokenProvider.validateRecoveryToken(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        // Obtener el email del token
        String email = jwtTokenProvider.getUsernameFromToken(token);

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        authService.changePassword(user, newPassword, confirmPassword);

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
