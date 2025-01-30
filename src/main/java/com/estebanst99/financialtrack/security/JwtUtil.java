package com.estebanst99.financialtrack.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilidad para la generación, validación y extracción de información de tokens JWT.
 */
@Component
public class JwtUtil {

    // Clave secreta para firmar el token JWT
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tiempo de expiración del token en milisegundos (10 días)
    private static final long JWT_EXPIRATION = 864_000_000;

    /**
     * Genera un token JWT para un usuario dado.
     *
     * @param username Nombre de usuario para el que se genera el token.
     * @return Token JWT generado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     *
     * @param token Token JWT del cual se extraerá el nombre de usuario.
     * @return Nombre de usuario contenido en el token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida si el token es correcto y pertenece al usuario dado.
     *
     * @param token       Token JWT a validar.
     * @param userDetails Detalles del usuario contra el cual se valida el token.
     * @return {@code true} si el token es válido, {@code false} en caso contrario.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param token Token JWT a verificar.
     * @return {@code true} si el token ha expirado, {@code false} si aún es válido.
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
