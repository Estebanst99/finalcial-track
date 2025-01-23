package com.estebanst99.financialtrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF si no es necesario
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Permitir acceso público a Swagger
                        .anyRequest().authenticated() // Requerir autenticación para otros endpoints
                )
                .formLogin(form -> form.permitAll()) // Habilitar el formulario de login
                .logout(logout -> logout.permitAll()); // Habilitar logout público

        return http.build();
    }
}
