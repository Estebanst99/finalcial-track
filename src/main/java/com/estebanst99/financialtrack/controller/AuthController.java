package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.UserServiceException;
import com.estebanst99.financialtrack.security.JwtUtil;
import com.estebanst99.financialtrack.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que gestiona los endpoints de autenticación.
 * Permite a los usuarios registrarse y autenticarse para obtener un token JWT.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructor que inyecta los servicios necesarios.
     *
     * @param userService Servicio para gestionar usuarios.
     * @param passwordEncoder Codificador de contraseñas.
     * @param jwtUtil Utilidad para generar y manejar tokens JWT.
     */
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint para autenticar a un usuario.
     * Si las credenciales son correctas, devuelve un token JWT.
     *
     * @param user Objeto {@link User} con las credenciales de acceso.
     * @return Una respuesta con el token JWT si las credenciales son válidas,
     *         o un mensaje de error si las credenciales son incorrectas.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            String token = jwtUtil.generateToken(existingUser.getEmail());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Credenciales incorrectas");
        }
    }

    /**
     * Endpoint para registrar a un nuevo usuario.
     * El usuario registrado recibirá un token JWT.
     *
     * @param user Objeto {@link User} con la información de registro.
     * @return Una respuesta con el token JWT generado para el nuevo usuario.
     * @throws UserServiceException Si ocurre un error al registrar al usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) throws UserServiceException {
        // Codificar la contraseña antes de guardar el usuario.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        // Generar un token JWT para el nuevo usuario.
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.status(201).body(token);
    }
}
