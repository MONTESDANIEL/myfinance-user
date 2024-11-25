package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.ChangePasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // USUARIO AUTENTICADO (NO FUNCIONA AÚN)

    // Obtener perfil del usuario autenticado
    @GetMapping("/profile")
    public ResponseEntity<AppUser> getUserProfile(@AuthenticationPrincipal AppUser authenticatedUser) {
        return userService.findById(authenticatedUser.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Editar perfil del usuario autenticado
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@AuthenticationPrincipal AppUser authenticatedUser,
            @Valid @RequestBody AppUser user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(userService.extractErrors(result));
        }
        return userService.updateUser(authenticatedUser.getId(), user);
    }

    // Cambiar la contraseña del usuario autenticado
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal AppUser authenticatedUser,
            @RequestBody ChangePasswordRequest changePasswordRequest) {

        return userService.changePassword(authenticatedUser,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmPassword());
    }

    // Eliminar usuario autenticado
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal AppUser authenticatedUser) {
        return userService.deleteUser(authenticatedUser.getId());
    }

    // ADMINISTRADOR

    // Solicitar un usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar usuario por id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    // Solicitar todos los usuarios
    @GetMapping
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
