package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los presupuestos.
 */
@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Constructor que inyecta el servicio de presupuestos.
     *
     * @param budgetService Servicio para gestionar presupuestos.
     */
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Endpoint para obtener todos los presupuestos del usuario autenticado.
     *
     * @return Una respuesta HTTP que contiene la lista de presupuestos del usuario.
     */
    @GetMapping
    public ResponseEntity<List<Budget>> getBudgets() {
        String userEmail = getAuthenticatedUserEmail();
        List<Budget> budgets = budgetService.findByUserEmail(userEmail);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Endpoint para crear o actualizar un presupuesto.
     * Si ya existe un presupuesto para la misma categoría, se actualiza.
     *
     * @param budget Objeto {@link Budget} con los datos del presupuesto a crear o actualizar.
     * @return Una respuesta HTTP con el presupuesto creado o actualizado, o una respuesta de error si falla la validación.
     */
    @PostMapping
    public ResponseEntity<Budget> saveBudget(@RequestBody Budget budget) {
        try {
            String userEmail = getAuthenticatedUserEmail();
            Budget savedBudget = budgetService.save(budget, userEmail);
            return ResponseEntity.status(201).body(savedBudget);
        } catch (BudgetServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para obtener el porcentaje de cumplimiento de un presupuesto basado en el total gastado.
     *
     * @param budgetId ID del presupuesto.
     * @return Una respuesta HTTP con el porcentaje de cumplimiento, o una respuesta de error si no se encuentra el presupuesto.
     */
    @GetMapping("/completion/{budgetId}")
    public ResponseEntity<Double> getBudgetCompletion(@PathVariable Long budgetId) {
        try {
            double completionPercentage = budgetService.getBudgetCompletionPercentage(budgetId);
            return ResponseEntity.ok(completionPercentage);
        } catch (BudgetServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene el email del usuario autenticado desde el contexto de seguridad.
     *
     * @return Email del usuario autenticado.
     */
    private String getAuthenticatedUserEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}