package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.PasswordRecovery;
import com.myfinance.backend.users.entities.security.RegisterRequest;
import com.myfinance.backend.users.entities.user.User;
import com.myfinance.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginRequest loginRequest) {
        // Busca al usuario por correo electrónico
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Correo electrónico no encontrado"));

        // Verifica si la contraseña ingresada coincide con la almacenada
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        String token = "generated-token";
        return token;
    }

    public boolean register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado");
        }

        User user = new User();
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
