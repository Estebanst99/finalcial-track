package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Obtiene presupuestos por usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de presupuestos.
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
     * Crea o actualiza un presupuesto.
     *
     * @param budget Presupuesto a guardar.
     * @return Presupuesto creado o actualizado.
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