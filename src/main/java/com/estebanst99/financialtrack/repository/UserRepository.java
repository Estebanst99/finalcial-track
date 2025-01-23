package com.estebanst99.financialtrack.repository;


import com.estebanst99.financialtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Proporciona métodos para gestionar usuarios, incluyendo búsqueda por email.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Encuentra un usuario por su email.
     *
     * @param email Email del usuario.
     * @return Optional<User> Usuario encontrado o vacío si no existe.
     */
    Optional<User> findByEmail(String email);
}
