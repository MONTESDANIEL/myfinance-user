package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> findAll() {
        return (List<AppUser>) userRepository.findAll();
    }

    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }

    public ResponseEntity<?> updateUser(Long userId, AppUser user) {
        // Verificar si el usuario existe
        Optional<AppUser> existingUser = userRepository.findById(userId);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser updatedUser = existingUser.get();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setName(user.getName());
        updatedUser.setPhoneNumber(user.getPhoneNumber());

        try {
            updatedUser = userRepository.save(updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("El correo electrónico ya está en uso. Por favor, usa otro.");
        }
    }

    public ResponseEntity<Void> deleteUser(Long id) {
        Optional<AppUser> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(existingUser.get());
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> changePassword(AppUser authenticatedUser, String currentPassword, String newPassword,
            String confirmPassword) {
        // Verificar si la contraseña actual es correcta
        if (!passwordEncoder.matches(currentPassword, authenticatedUser.getPassword())) {
            return ResponseEntity.status(403).body("La contraseña actual es incorrecta.");
        }

        // Verificar que la nueva contraseña y la confirmación coinciden
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.status(400).body("Las contraseñas no coinciden.");
        }

        // Validar que la nueva contraseña tenga una longitud adecuada
        if (newPassword.length() < 6) {
            return ResponseEntity.status(400).body("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        // Actualizar la contraseña
        authenticatedUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(authenticatedUser);

        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }

    // Extraer errores de validación y devolverlos
    public List<String> extractErrors(BindingResult result) {
        return result.getAllErrors().stream()
                .map(error -> {
                    String field = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    return field + ": " + message;
                })
                .collect(Collectors.toList());
    }
}
