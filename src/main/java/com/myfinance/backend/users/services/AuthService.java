package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.security.AppUserDetails;
import com.myfinance.backend.users.entities.security.LoginRequest;
import com.myfinance.backend.users.entities.security.PasswordRecovery;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequest loginRequest) {

        // Busca al usuario por correo electrónico
        AppUser user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Correo electrónico no encontrado"));

        // Verifica si la contraseña ingresada coincide con la almacenada
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        // Crea el objeto AppUserDetails
        AppUserDetails userDetails = new AppUserDetails(user);

        // Autentica al usuario manualmente
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Genera el token JWT
        String jwtToken = jwtTokenProvider.createToken(user.getEmail());

        // Retorna el token JWT
        return jwtToken;
    }

    public boolean register(AppUser appUser) {
        if (userRepository.findByEmail(appUser.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado");
        }

        AppUser user = new AppUser();
        user.setId(appUser.getId());
        user.setName(appUser.getName());
        user.setEmail(appUser.getEmail());
        user.setPhoneNumber(appUser.getPhoneNumber());
        user.setBirthDate(appUser.getBirthDate());

        user.setPassword(passwordEncoder.encode(appUser.getPassword()));

        userRepository.save(user);
        return true;
    }

    public boolean recoverPassword(PasswordRecovery recoveryRequest) {
        return true;
    }

    public ResponseEntity<?> resetPassword(AppUser user, String password, String confirmPassword) {
        // Validar que la nueva contraseña tenga una longitud adecuada
        if (password.length() < 6) {
            return ResponseEntity.status(400).body("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        // Verificar que la nueva contraseña y la confirmación coinciden
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(400).body("Las contraseñas no coinciden.");
        }

        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
