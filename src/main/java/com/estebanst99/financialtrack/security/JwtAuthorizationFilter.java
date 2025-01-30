package com.estebanst99.financialtrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autorización JWT que se ejecuta una vez por solicitud.
 * Este filtro se encarga de validar el token JWT presente en la solicitud y
 * autenticar al usuario en el contexto de seguridad de Spring Security.
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param jwtUtil            Utilidad para manejar operaciones con tokens JWT.
     * @param userDetailsService Servicio para cargar detalles del usuario.
     */
    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Método que procesa la solicitud entrante, verificando el encabezado de autorización.
     * Si el token es válido, se autentica al usuario en el contexto de seguridad.
     *
     * @param request     Solicitud HTTP entrante.
     * @param response    Respuesta HTTP saliente.
     * @param filterChain Cadena de filtros que continúa el procesamiento de la solicitud.
     * @throws ServletException Si ocurre un error durante el procesamiento del filtro.
     * @throws IOException      Si ocurre un error de entrada/salida durante el procesamiento.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtiene el encabezado de autorización de la solicitud
        String authHeader = request.getHeader("Authorization");

        // Verifica que el encabezado no sea nulo y comience con "Bearer"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extrae el token JWT
            String email = jwtUtil.extractUsername(token); // Extrae el email del token

            // Verifica que no haya una autenticación previa en el contexto de seguridad
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(email);

                // Valida el token y, si es válido, autentica al usuario
                if (jwtUtil.validateToken(token, userDetails)) {
                    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        // Continúa con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
