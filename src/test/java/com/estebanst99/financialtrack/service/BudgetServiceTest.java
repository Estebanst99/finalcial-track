package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Budget;
import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.repository.BudgetRepository;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @InjectMocks
    private BudgetService budgetService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_NewBudget() throws BudgetServiceException {
        Budget budget = new Budget();
        budget.setCategory(new Category());
        budget.setUser(new User());
        budget.setLimit(1000.0);
        budget.setStartDate(LocalDate.now());
        budget.setEndDate(LocalDate.now().plusDays(30));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        when(budgetRepository.findByUserEmailAndCategoryId("test@example.com", budget.getCategory().getId()))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(budget)).thenReturn(budget);

        Budget savedBudget = budgetService.save(budget, "test@example.com");

        assertNotNull(savedBudget);
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void testSave_UpdateExistingBudget() throws BudgetServiceException {
        Budget budget = new Budget();
        budget.setCategory(new Category());
        budget.setLimit(2000.0);
        budget.setStartDate(LocalDate.now());
        budget.setEndDate(LocalDate.now().plusDays(30));

        Budget existingBudget = new Budget();
        existingBudget.setLimit(1000.0);

        when(budgetRepository.findByUserEmailAndCategoryId("test@example.com", budget.getCategory().getId()))
                .thenReturn(Optional.of(existingBudget));
        when(budgetRepository.save(existingBudget)).thenReturn(existingBudget);

        Budget updatedBudget = budgetService.save(budget, "test@example.com");

        assertEquals(2000.0, updatedBudget.getLimit());
        verify(budgetRepository, times(1)).save(existingBudget);
    }

    @Test
    void testFindByUserEmail_Success() {
        List<Budget> budgets = Arrays.asList(new Budget(), new Budget());
        when(budgetRepository.findByUserEmail("test@example.com")).thenReturn(budgets);

        List<Budget> result = budgetService.findByUserEmail("test@example.com");

        assertEquals(2, result.size());
    }

    @Test
    void testFindByUserEmailAndCategoryId_Success() throws BudgetServiceException {
        Budget budget = new Budget();
        when(budgetRepository.findByUserEmailAndCategoryId("test@example.com", 1L))
                .thenReturn(Optional.of(budget));

        Budget result = budgetService.findByUserEmailAndCategoryId("test@example.com", 1L);

        assertNotNull(result);
    }

    @Test
    void testFindByUserEmailAndCategoryId_NotFound() {
        when(budgetRepository.findByUserEmailAndCategoryId("test@example.com", 1L))
                .thenReturn(Optional.empty());

        assertThrows(BudgetServiceException.class, () -> budgetService.findByUserEmailAndCategoryId("test@example.com", 1L));
    }

    @Test
    void testDeleteById_Success() throws BudgetServiceException {
        when(budgetRepository.existsById(1L)).thenReturn(true);

        budgetService.deleteById(1L);

        verify(budgetRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(budgetRepository.existsById(1L)).thenReturn(false);

        assertThrows(BudgetServiceException.class, () -> budgetService.deleteById(1L));
    }

    @Test
    void testGetBudgetCompletionPercentage_Success() throws BudgetServiceException {
        Budget budget = new Budget();
        budget.setLimit(1000.0);

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        double percentage = budgetService.getBudgetCompletionPercentage(1L);

        assertEquals(0.0, percentage); // Simula lógica de cálculo
    }
}
