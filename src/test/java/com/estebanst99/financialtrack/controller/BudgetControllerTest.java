package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBudgetsByUserId_Success() throws BudgetServiceException {
        Long userId = 1L;
        List<Budget> budgets = Collections.singletonList(new Budget());
        when(budgetService.findByUserId(userId)).thenReturn(budgets);

        ResponseEntity<List<Budget>> response = budgetController.getBudgetsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budgets, response.getBody());
    }

    @Test
    void testGetBudgetsByUserId_Failure() throws BudgetServiceException {
        Long userId = 1L;
        when(budgetService.findByUserId(userId)).thenThrow(new BudgetServiceException("Error"));

        ResponseEntity<List<Budget>> response = budgetController.getBudgetsByUserId(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSaveBudget_Success() throws BudgetServiceException {
        Budget budget = new Budget();
        Budget savedBudget = new Budget();
        when(budgetService.save(budget)).thenReturn(savedBudget);

        ResponseEntity<Budget> response = budgetController.saveBudget(budget);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedBudget, response.getBody());
    }

    @Test
    void testSaveBudget_Failure() throws BudgetServiceException {
        Budget budget = new Budget();
        when(budgetService.save(budget)).thenThrow(new BudgetServiceException("Error"));

        ResponseEntity<Budget> response = budgetController.saveBudget(budget);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}