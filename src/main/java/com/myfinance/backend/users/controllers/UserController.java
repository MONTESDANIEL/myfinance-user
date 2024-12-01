package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.ChangePasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Ver usuario: Proporciona los datos del usuario autenticado
    @GetMapping("/view")
    public ResponseEntity<?> getUser() {
        ResponseEntity<?> response = userService.getUser();
        return response;
    }

    // Actualizar usuario: Actualiza los datos del usuario autenticado
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody AppUser user) {
        ResponseEntity<?> response = userService.updateUser(user);
        return response;
    }

    // Actualizar contraseña: Actualiza la contraseña del usuario autenticado
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        ResponseEntity<?> response = userService.changePassword(changePasswordRequest);
        return response;
    }

    // Elimina al usuario autenticado
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        return userService.deleteUser();
    }

}
