package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.PasswordRecovery;
import com.myfinance.backend.users.entities.security.RegisterRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public String login(LoginRequest loginRequest) {
        // Busca al usuario por correo electrónico
        AppUser user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Correo electrónico no encontrado"));

        // Verifica si la contraseña ingresada coincide con la almacenada
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        // Autentica al usuario manualmente y almacena el contexto de seguridad
        // (opcional)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("Contexto del login: " + SecurityContextHolder.getContext().getAuthentication());

        // Genera el token JWT
        String jwtToken = jwtTokenProvider.createToken(user.getEmail());

        // Retorna el token JWT
        return jwtToken;
    }

    public boolean register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado");
        }

        AppUser user = new AppUser();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setBirthDate(registerRequest.getBirthDate());

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);
        return true;
    }

    public boolean recoverPassword(PasswordRecovery recoveryRequest) {
        return true;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
