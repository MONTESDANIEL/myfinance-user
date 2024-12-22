package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.security.ApiResponse;
import com.myfinance.backend.users.entities.security.AppUserDetails;
import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.ResetPasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> login(LoginRequest loginRequest) {

        Optional<AppUser> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        // Validación de la existencia del usuario
        if (userOptional.isEmpty()) {
            return createApiResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.", null);
        }

        AppUser user = userOptional.get();

        // Validación de la contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return createApiResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.", null);
        }

        // Crea el objeto AppUserDetails para autenticar al usuario
        AppUserDetails userDetails = new AppUserDetails(user);

        // Autentica al usuario manualmente
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Genera el token JWT
        String jwtToken = jwtTokenProvider.createToken(user.getEmail());

        // Respuesta exitosa con el JWT
        return createApiResponse(HttpStatus.ACCEPTED, "Login exitoso.", jwtToken);
    }

    public ResponseEntity<?> register(AppUser appUser) {

        if (userRepository.findById(appUser.getId()).isPresent()) {
            return createApiResponse(HttpStatus.CONFLICT, "El usuario ya está registrado.", null);
        }

        if (userRepository.findByEmail(appUser.getEmail()).isPresent()) {
            return createApiResponse(HttpStatus.CONFLICT, "El correo electrónico ya está registrado.", null);
        }

        if (userRepository.findByPhoneNumber(appUser.getPhoneNumber()).isPresent()) {
            return createApiResponse(HttpStatus.CONFLICT, "El número ya esta registrado.", null);
        }

        appUser.setName(Arrays.stream(appUser.getName().split(" "))
                .map(palabra -> palabra.substring(0, 1).toUpperCase() + palabra.substring(1).toLowerCase())
                .collect(Collectors.joining(" ")));

        appUser.setEmail(appUser.getEmail().trim().toLowerCase());

        AppUser user = new AppUser();
        user.setId(appUser.getId());
        user.setIdType(appUser.getIdType());
        user.setName(appUser.getName());
        user.setEmail(appUser.getEmail());
        user.setPhoneNumber(appUser.getPhoneNumber());
        user.setBirthDate(appUser.getBirthDate());
        user.setPassword(passwordEncoder.encode(appUser.getPassword()));

        userRepository.save(user);
        return createApiResponse(HttpStatus.CREATED, "Usuario registrado con exito.", null);
    }

    // Enviar el correo de recuperación
    public ResponseEntity<?> recoverPassword(String email) {

        Optional<AppUser> userOptional = userRepository.findByEmail(email);

        // Validación de la existencia del usuario
        if (userOptional.isEmpty()) {
            return createApiResponse(HttpStatus.UNAUTHORIZED, "Correo no registrado.", null);
        }

        // Generar el token de recuperación
        String token = jwtTokenProvider.createRecoveryToken(email);

        try {
            emailService.sendRecoveryEmail(email, token);
            return createApiResponse(HttpStatus.OK, "Correo de recuperación enviado con éxito.", token);
        } catch (Exception e) {
            return createApiResponse(HttpStatus.BAD_REQUEST, "Error al enviar el correo de recuperación.", null);
        }
    }

    // Cambio de la contraseña
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {

        // Validar el token de recuperación
        if (!jwtTokenProvider.validateRecoveryToken(request.getToken())) {
            return createApiResponse(HttpStatus.UNAUTHORIZED, "Token inválido o expirado", null);
        }

        // Extraer usuario del token
        AppUser user = userRepository
                .findByEmail(jwtTokenProvider.getUsernameFromToken(request.getToken())).get();

        // Validación de la contraseña
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return createApiResponse(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden.", null);
        }

        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return createApiResponse(HttpStatus.OK, "La contraseña fue cambiada con exito.", null);
    }

    // Inhabilitar el token
    public ResponseEntity<?> logout(String token) {

        // Validar el token de recuperación
        if (token == null) {
            return createApiResponse(HttpStatus.BAD_REQUEST,
                    "No se envio el token. Por favor, asegúrese de que sea enviado correctamente.", null);
        }

        // Validar el token de recuperación
        if (!jwtTokenProvider.validateToken(token)) {
            return createApiResponse(HttpStatus.UNAUTHORIZED, "Token inválido o expirado.", null);
        }

        // Revocar el token, es decir, agregarlo a la lista de tokens revocados
        jwtTokenProvider.revokeToken(token);

        SecurityContextHolder.clearContext();

        // Responder indicando que el logout fue exitoso
        return createApiResponse(HttpStatus.OK, "Logout exitoso.", null);
    }

    // Metodo para crear una respuesta con formato
    private ResponseEntity<?> createApiResponse(HttpStatus status, String message, Object data) {
        ApiResponse<Object> response = new ApiResponse<>(message, data);
        return ResponseEntity.status(status).body(response);
    }

}