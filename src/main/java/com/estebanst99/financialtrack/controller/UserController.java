package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.UserServiceException;
import com.estebanst99.financialtrack.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email Email del usuario.
     * @return Usuario encontrado o 404 si no existe.
     */
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "El usuario con el email " + email + " no existe."));
        }
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param user Usuario a crear.
     * @return Usuario creado.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Verificar si ya existe un usuario con el mismo email
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "El usuario con el email " + user.getEmail() + " ya existe."));
        }

        try {
            User savedUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al guardar el usuario: " + e.getMessage()));
        }
    }
}
