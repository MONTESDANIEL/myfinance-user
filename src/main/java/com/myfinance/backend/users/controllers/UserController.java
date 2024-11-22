package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.User;
import com.myfinance.backend.users.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Incorporación de versionado
@RequiredArgsConstructor
public class UserController { // Nombre en singular

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Solicitud para obtener todos los usuarios");
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Solicitud para obtener usuario con id {}", id);
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Usuario con id {} no encontrado", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Solicitud para crear un nuevo usuario");
        User savedUser = userService.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        logger.info("Solicitud para actualizar usuario con id {}", id);
        return userService.findById(id).map(existingUser -> {
            user.setId(id); // Asignar el ID del usuario existente
            User updatedUser = userService.save(user);
            return ResponseEntity.ok(updatedUser);
        }).orElseGet(() -> {
            logger.warn("Usuario con id {} no encontrado para actualizar", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Solicitud para eliminar usuario con id {}", id);
        return userService.findById(id).map(user -> {
            userService.delete(id);
            logger.info("Usuario con id {} eliminado exitosamente", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> {
            logger.warn("Usuario con id {} no encontrado para eliminar", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }
}
