package com.myfinance.backend.users.services;

import com.myfinance.backend.users.entities.security.ApiResponse;
import com.myfinance.backend.users.entities.security.AppUserDetails;
import com.myfinance.backend.users.entities.security.ChangePasswordRequest;
import com.myfinance.backend.users.entities.user.AppUser;
import com.myfinance.backend.users.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> getUser() {
        AppUser user = getAuthenticatedAppUser();

        if (user == null) {
            return createApiResponse(HttpStatus.BAD_REQUEST, "No se pudo obtener la información del usuario.", null);
        } else {
            return createApiResponse(HttpStatus.OK, "Información del usuario cargada correctamente.", user);
        }
    }

    public ResponseEntity<?> updateUser(AppUser user) {

        AppUser appUser = getAuthenticatedAppUser();

        if (appUser == null) {
            return createApiResponse(HttpStatus.BAD_REQUEST, "No se pudo obtener la información del usuario.", null);
        } else {
            if (!appUser.getId().equals(user.getId())) {
                return createApiResponse(HttpStatus.BAD_REQUEST, "No se pudieron actualizar los datos del usuario",
                        appUser.getId());
            }

            // Aquí puedes decidir qué campos quieres actualizar. Ejemplo:
            appUser.setEmail(user.getEmail());
            appUser.setPhoneNumber(user.getPhoneNumber());

            // Guarda los cambios en la base de datos
            userRepository.save(appUser);

            return createApiResponse(HttpStatus.OK, "Se actualizó la información del usuario.", null);
        }

    }

    public ResponseEntity<?> changePassword(ChangePasswordRequest passwordRequest) {

        AppUser appUser = getAuthenticatedAppUser();

        if (appUser == null) {
            return createApiResponse(HttpStatus.BAD_REQUEST, "No se pudo obtener la información del usuario.", null);
        } else {
            // Verificar si la contraseña actual es correcta
            if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), appUser.getPassword())) {
                return createApiResponse(HttpStatus.UNAUTHORIZED,
                        "La contraseña actual proporcionada es incorrecta. Por favor, intente nuevamente.", null);
            }

            // Verificar que la nueva contraseña y la confirmación coinciden
            if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
                return createApiResponse(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden.", null);
            }

            // Actualizar la contraseña
            appUser.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            userRepository.save(appUser);

            return createApiResponse(HttpStatus.OK, "La contraseña se actualizó con exito.", null);
        }

    }

    public ResponseEntity<?> deleteUser() {

        AppUser user = getAuthenticatedAppUser();

        if (user == null) {
            return createApiResponse(HttpStatus.BAD_REQUEST, "No se pudo obtener la información del usuario.", null);
        } else {
            userRepository.delete(user);
            return createApiResponse(HttpStatus.NO_CONTENT, "El usuario se elimino correctamente.", null);
        }

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

    // Verifica si el usuario está autenticado y si es de tipo AppUserDetails
    public AppUser getAuthenticatedAppUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof AppUserDetails)) {
            AppUser user = new AppUser();
            return user;
        }

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        return userDetails.getAppUser();
    }

    // Crea una respuesta segùn el formato
    private ResponseEntity<ApiResponse<Object>> createApiResponse(HttpStatus status, String message, Object data) {
        ApiResponse<Object> response = new ApiResponse<>(message, data);
        return ResponseEntity.status(status).body(response);
    }
}
