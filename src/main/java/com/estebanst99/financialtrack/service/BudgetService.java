package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.repository.BudgetRepository;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor para inyectar los repositorios.
     */
    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea o actualiza un presupuesto.
     *
     * @param budget Presupuesto a guardar.
     * @return Presupuesto creado o actualizado.
     * @throws BudgetServiceException Si los datos del presupuesto son inválidos.
     */
    @Transactional
    public Budget save(Budget budget) throws BudgetServiceException {
        validateBudget(budget);

        // Verifica si ya existe un presupuesto para el usuario y la categoría
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategoryId(
                budget.getUser().getId(), budget.getCategory().getId()
        );

        if (existingBudget.isPresent()) {
            Budget budgetToUpdate = existingBudget.get();
            budgetToUpdate.setLimit(budget.getLimit());
            budgetToUpdate.setStartDate(budget.getStartDate());
            budgetToUpdate.setEndDate(budget.getEndDate());
            return budgetRepository.save(budgetToUpdate);
        }

        return budgetRepository.save(budget);
    }

    /**
     * Recupera todos los presupuestos de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de presupuestos.
     * @throws BudgetServiceException Si no se encuentran presupuestos.
     */
    public List<Budget> findByUserId(Long userId) throws BudgetServiceException {
        if (!userRepository.existsById(userId)) {
            throw new BudgetServiceException("El usuario no existe.");
        }

        List<Budget> budgets = budgetRepository.findByUserId(userId);
        if (budgets.isEmpty()) {
            throw new BudgetServiceException("No se encontraron presupuestos para el usuario.");
        }

        return budgets;
    }

    /**
     * Recupera un presupuesto por usuario y categoría.
     *
     * @param userId     ID del usuario.
     * @param categoryId ID de la categoría.
     * @return Presupuesto encontrado.
     * @throws BudgetServiceException Si el presupuesto no existe.
     */
    public Budget findByUserIdAndCategoryId(Long userId, Long categoryId) throws BudgetServiceException {
        if (!userRepository.existsById(userId)) {
            throw new BudgetServiceException("El usuario no existe.");
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new BudgetServiceException("La categoría no existe.");
        }

        return budgetRepository.findByUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new BudgetServiceException("El presupuesto no existe para el usuario y categoría especificados."));
    }

    /**
     * Elimina un presupuesto por su ID.
     *
     * @param id ID del presupuesto.
     * @throws BudgetServiceException Si el presupuesto no existe.
     */
    @Transactional
    public void deleteById(Long id) throws BudgetServiceException {
        if (!budgetRepository.existsById(id)) {
            throw new BudgetServiceException("El presupuesto no existe.");
        }

        budgetRepository.deleteById(id);
    }

    /**
     * Valida los datos de un presupuesto.
     *
     * @param budget Presupuesto a validar.
     * @throws BudgetServiceException Si los datos son inválidos.
     */
    private void validateBudget(Budget budget) throws BudgetServiceException {
        if (budget == null) {
            throw new BudgetServiceException("El presupuesto no puede ser nulo.");
        }
        if (budget.getLimit() == null || budget.getLimit() <= 0) {
            throw new BudgetServiceException("El límite del presupuesto debe ser positivo.");
        }
        if (budget.getUser() == null || budget.getUser().getId() == null) {
            throw new BudgetServiceException("El presupuesto debe estar asociado a un usuario válido.");
        }
        if (budget.getCategory() == null || budget.getCategory().getId() == null) {
            throw new BudgetServiceException("El presupuesto debe estar asociado a una categoría válida.");
        }
        if (budget.getStartDate() == null || budget.getEndDate() == null) {
            throw new BudgetServiceException("El presupuesto debe tener un rango de fechas válido.");
        }
        if (budget.getEndDate().isBefore(budget.getStartDate())) {
            throw new BudgetServiceException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }
}