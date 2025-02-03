package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.repository.BudgetRepository;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar operaciones relacionadas con los presupuestos.
 */
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Constructor para inicializar los repositorios necesarios.
     *
     * @param budgetRepository   Repositorio para operaciones de presupuesto.
     * @param categoryRepository Repositorio para operaciones de categorías.
     * @param userRepository     Repositorio para operaciones de usuarios.
     */
    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Guarda un presupuesto en la base de datos. Si ya existe un presupuesto para el usuario y la categoría,
     * actualiza el existente.
     *
     * @param budget    Presupuesto a guardar o actualizar.
     * @param userEmail Email del usuario autenticado.
     * @return El presupuesto guardado o actualizado.
     * @throws BudgetServiceException Si hay errores de validación o el usuario no existe.
     */
    @Transactional
    public Budget save(Budget budget, String userEmail) throws BudgetServiceException {
        validateBudget(budget);

        Optional<Budget> existingBudget = budgetRepository.findByUserEmailAndCategoryId(
                userEmail, budget.getCategory().getId()
        );

        if (existingBudget.isPresent()) {
            Budget budgetToUpdate = existingBudget.get();
            budgetToUpdate.setLimit(budget.getLimit());
            budgetToUpdate.setStartDate(budget.getStartDate());
            budgetToUpdate.setEndDate(budget.getEndDate());
            return budgetRepository.save(budgetToUpdate);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BudgetServiceException("Usuario no encontrado."));
        budget.setUser(user);

        return budgetRepository.save(budget);
    }

    /**
     * Busca todos los presupuestos asociados a un usuario.
     *
     * @param userEmail Email del usuario autenticado.
     * @return Lista de presupuestos del usuario.
     */
    public List<Budget> findByUserEmail(String userEmail) {
        return budgetRepository.findByUserEmail(userEmail);
    }

    /**
     * Busca un presupuesto específico por el email del usuario y el ID de la categoría.
     *
     * @param userEmail  Email del usuario autenticado.
     * @param categoryId ID de la categoría del presupuesto.
     * @return El presupuesto encontrado.
     * @throws BudgetServiceException Si no se encuentra el presupuesto.
     */
    public Budget findByUserEmailAndCategoryId(String userEmail, Long categoryId) throws BudgetServiceException {
        return budgetRepository.findByUserEmailAndCategoryId(userEmail, categoryId)
                .orElseThrow(() -> new BudgetServiceException("Presupuesto no encontrado."));
    }

    /**
     * Calcula el porcentaje de cumplimiento de un presupuesto basado en el total gastado en su categoría.
     *
     * @param budgetId ID del presupuesto.
     * @return Porcentaje de cumplimiento (entre 0 y 100).
     * @throws BudgetServiceException Si no se encuentra el presupuesto.
     */
    public double getBudgetCompletionPercentage(Long budgetId) throws BudgetServiceException {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetServiceException("Presupuesto no encontrado."));

        // Lógica adicional para obtener el total gastado en la categoría del presupuesto
        double totalSpent = 0.0; // Simulación, implementar según negocio
        return (totalSpent / budget.getLimit()) * 100;
    }

    /**
     * Elimina un presupuesto por su ID.
     *
     * @param id ID del presupuesto.
     * @throws BudgetServiceException Si no se encuentra el presupuesto.
     */
    public void deleteById(Long id) throws BudgetServiceException {
        if (!budgetRepository.existsById(id)) {
            throw new BudgetServiceException("Presupuesto no encontrado.");
        }
        budgetRepository.deleteById(id);
    }

    /**
     * Valida los datos de un presupuesto.
     *
     * @param budget Presupuesto a validar.
     * @throws BudgetServiceException Si los datos del presupuesto son inválidos.
     */
    private void validateBudget(Budget budget) throws BudgetServiceException {
        if (budget.getLimit() == null || budget.getLimit() <= 0) {
            throw new BudgetServiceException("El límite del presupuesto debe ser positivo.");
        }
        if (budget.getStartDate().isAfter(budget.getEndDate())) {
            throw new BudgetServiceException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }
}