package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que gestiona los endpoints relacionados con los presupuestos.
 * Los usuarios pueden obtener, crear o actualizar sus presupuestos.
 */
@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Constructor que inyecta el servicio de presupuesto.
     *
     * @param budgetService Servicio para gestionar presupuestos.
     */
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Endpoint para obtener todos los presupuestos asociados a un usuario.
     *
     * @param userId ID del usuario.
     * @return Una respuesta con la lista de presupuestos del usuario, o una respuesta de error.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable Long userId) {
        try {
            List<Budget> budgets = budgetService.findByUserId(userId);
            return ResponseEntity.ok(budgets);
        } catch (BudgetServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para crear o actualizar un presupuesto.
     * Si se proporciona un ID existente en el presupuesto, se actualiza ese registro.
     * Si no, se crea un nuevo presupuesto.
     *
     * @param budget Objeto {@link Budget} con los datos del presupuesto a crear o actualizar.
     * @return Una respuesta con el presupuesto creado o actualizado, o una respuesta de error.
     */
    @PostMapping
    public ResponseEntity<Budget> saveBudget(@RequestBody Budget budget) {
        try {
            Budget savedBudget = budgetService.save(budget);
            return ResponseEntity.status(201).body(savedBudget);
        } catch (BudgetServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
