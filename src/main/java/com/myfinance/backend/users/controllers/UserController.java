package com.myfinance.backend.users.controllers;

import com.myfinance.backend.users.entities.security.AppUserDetails;
import com.myfinance.backend.users.entities.security.ChangePasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/view")
    public ResponseEntity<?> getCurrentUser() {
        try {
            AppUser appUser = getAuthenticatedAppUser();
            return ResponseEntity.ok(appUser);
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody AppUser user) {
        try {
            AppUser appUser = getAuthenticatedAppUser();
            userService.updateUser(user, appUser.getId());
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado exitosamente"));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ha ocurrido un error inesperado", "details", ex.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            AppUser appUser = getAuthenticatedAppUser();
            return userService.changePassword(
                    appUser,
                    changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword(),
                    changePasswordRequest.getConfirmPassword());
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        try {
            AppUser appUser = getAuthenticatedAppUser();
            return userService.deleteUser(appUser.getId());
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    private AppUser getAuthenticatedAppUser() throws UnauthorizedAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica si el usuario está autenticado y si es de tipo AppUserDetails
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof AppUserDetails)) {
            throw new UnauthorizedAccessException("No estás autenticado");
        }

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        return userDetails.getAppUser();
    }

    // Excepción personalizada para manejo de errores de autenticación
    public static class UnauthorizedAccessException extends Exception {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
}
