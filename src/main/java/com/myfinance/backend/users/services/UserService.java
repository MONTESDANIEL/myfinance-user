package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void updateUser(AppUser user, Long id) {
        // Verifica si el usuario existe en la base de datos
        Optional<AppUser> existingUserOptional = userRepository.findById(id);

        // Si el usuario existe, actualiza los campos necesarios
        AppUser existingUser = existingUserOptional.get();

        // Aquí puedes decidir qué campos quieres actualizar. Ejemplo:
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        // Puedes añadir más campos según sea necesario

        // Guarda los cambios en la base de datos
        userRepository.save(existingUser);
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
