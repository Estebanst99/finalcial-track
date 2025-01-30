package com.estebanst99.financialtrack.config;

import com.estebanst99.financialtrack.security.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para la aplicación.
 * Define la cadena de filtros de seguridad, los permisos de acceso y la configuración de autenticación.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    /**
     * Rutas que no requieren autenticación.
     */
    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",    // Endpoints de autenticación
            "/swagger-ui/**",     // Recursos de Swagger UI
            "/v3/api-docs/**",    // Documentación de OpenAPI
            "/swagger-ui.html"    // Entrada principal de Swagger
    };

    /**
     * Constructor que inyecta el filtro de autorización basado en JWT.
     *
     * @param jwtAuthorizationFilter Filtro de autorización JWT.
     */
    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad para la aplicación.
     *
     * @param http Objeto {@link HttpSecurity} para configurar la seguridad HTTP.
     * @return La configuración de la cadena de filtros de seguridad.
     * @throws Exception Si ocurre algún error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desactivar CSRF ya que usamos autenticación con JWT.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll() // Permitir acceso público a las rutas especificadas.
                        .anyRequest().authenticated()             // Requerir autenticación para cualquier otra solicitud.
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class); // Añadir el filtro de autorización JWT.

        return http.build();
    }

    /**
     * Crea un bean de codificador de contraseñas que utiliza el algoritmo BCrypt.
     *
     * @return Un objeto {@link PasswordEncoder} que utiliza BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crea un bean de administrador de autenticación basado en la configuración actual.
     *
     * @param configuration Configuración de autenticación.
     * @return El administrador de autenticación.
     * @throws Exception Si ocurre algún error al obtener el administrador de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
