package com.myfinance.backend.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfinance.backend.users.entities.User;
import com.myfinance.backend.users.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserControllers {

    private final UserService userService;  // Eliminar @Autowired en el campo, inyección por constructor

    // Inyección por constructor
    public UserControllers(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/view")
    public List<User> list() {
        return userService.findAll();  // Llamar al servicio para obtener los usuarios
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> view(@PathVariable Long id){
        Optional<User> userOptional = userService.findById(id);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
    System.out.println("Datos recibidos: " + user);
    if (result.hasErrors()) {
        List<String> errors = result.getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    userService.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
}


}
