package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.UserServiceException;
import com.estebanst99.financialtrack.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para crear y consultar usuarios.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor que inyecta la dependencia del servicio de usuarios.
     *
     * @param userService Servicio de usuarios.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email Email del usuario que se desea buscar.
     * @return Una respuesta HTTP con el usuario encontrado y un código 200 (OK).
     *         Si no se encuentra el usuario, devuelve un código 404 (NOT_FOUND) con un mensaje de error.
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
     * Verifica si ya existe un usuario con el mismo email antes de proceder.
     *
     * @param user Objeto de tipo {@link User} con la información del usuario a crear.
     * @return Una respuesta HTTP con el usuario creado y un código 201 (CREATED).
     *         Si ya existe un usuario con el mismo email, devuelve un código 400 (BAD_REQUEST) con un mensaje.
     *         Si ocurre un error durante la creación, devuelve un código 500 (INTERNAL_SERVER_ERROR) con un mensaje de error.
     */
    /*@PostMapping
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
    }*/
}
