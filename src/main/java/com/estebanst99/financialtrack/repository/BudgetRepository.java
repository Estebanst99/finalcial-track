package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    /**
     * Encuentra presupuestos de un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Lista de presupuestos del usuario.
     */
    List<Budget> findByUserId(Long userId);

    /**
     * Encuentra un presupuesto específico de un usuario en una categoría.
     *
     * @param userId     ID del usuario.
     * @param categoryId ID de la categoría.
     * @return Presupuesto encontrado o vacío si no existe.
     */
    Optional<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
}