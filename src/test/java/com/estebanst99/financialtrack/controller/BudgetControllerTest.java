package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BudgetControllerTest {

    @InjectMocks
    private BudgetController budgetController;

    @Mock
    private BudgetService budgetService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void testGetBudgets() {
        List<Budget> budgets = Arrays.asList(new Budget(), new Budget());
        when(budgetService.findByUserEmail("test@example.com")).thenReturn(budgets);

        ResponseEntity<List<Budget>> response = budgetController.getBudgets();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testSaveBudget_Success() throws BudgetServiceException {
        Budget budget = new Budget();
        when(budgetService.save(any(Budget.class), eq("test@example.com"))).thenReturn(budget);

        ResponseEntity<Budget> response = budgetController.saveBudget(budget);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testSaveBudget_Failure() throws BudgetServiceException {
        Budget budget = new Budget();
        when(budgetService.save(any(Budget.class), eq("test@example.com"))).thenThrow(new BudgetServiceException("Error"));

        ResponseEntity<Budget> response = budgetController.saveBudget(budget);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testGetBudgetCompletion_Success() throws BudgetServiceException {
        when(budgetService.getBudgetCompletionPercentage(1L)).thenReturn(75.0);

        ResponseEntity<Double> response = budgetController.getBudgetCompletion(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(75.0, response.getBody());
    }

    @Test
    void testGetBudgetCompletion_Failure() throws BudgetServiceException {
        when(budgetService.getBudgetCompletionPercentage(1L)).thenThrow(new BudgetServiceException("Error"));

        ResponseEntity<Double> response = budgetController.getBudgetCompletion(1L);

        assertEquals(400, response.getStatusCodeValue());
    }
}
