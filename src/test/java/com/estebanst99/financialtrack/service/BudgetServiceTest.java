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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_ValidBudget() throws BudgetServiceException {
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Budget budget = new Budget();
        budget.setLimit(1000.0);
        budget.setStartDate(LocalDate.now());
        budget.setEndDate(LocalDate.now().plusMonths(1));
        budget.setUser(user);
        budget.setCategory(category);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(budgetRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Optional.empty());
        when(budgetRepository.save(budget)).thenReturn(budget);

        Budget savedBudget = budgetService.save(budget);

        assertNotNull(savedBudget);
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void testSave_UpdateExistingBudget() throws BudgetServiceException {
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Budget existingBudget = new Budget();
        existingBudget.setId(1L);
        existingBudget.setLimit(500.0);
        existingBudget.setStartDate(LocalDate.now());
        existingBudget.setEndDate(LocalDate.now().plusMonths(1));
        existingBudget.setUser(user);
        existingBudget.setCategory(category);

        Budget updatedBudget = new Budget();
        updatedBudget.setLimit(1000.0);
        updatedBudget.setStartDate(LocalDate.now());
        updatedBudget.setEndDate(LocalDate.now().plusMonths(2));
        updatedBudget.setUser(user);
        updatedBudget.setCategory(category);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(budgetRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Optional.of(existingBudget));
        when(budgetRepository.save(existingBudget)).thenReturn(existingBudget);

        Budget result = budgetService.save(updatedBudget);

        assertNotNull(result);
        assertEquals(1000.0, result.getLimit());
        assertEquals(LocalDate.now().plusMonths(2), result.getEndDate());
        verify(budgetRepository, times(1)).save(existingBudget);
    }

    @Test
    void testFindByUserId_UserNotExists() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        BudgetServiceException exception = assertThrows(BudgetServiceException.class, () -> budgetService.findByUserId(userId));
        assertEquals("El usuario no existe.", exception.getMessage());
    }

    @Test
    void testFindByUserId_NoBudgetsFound() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of());

        BudgetServiceException exception = assertThrows(BudgetServiceException.class, () -> budgetService.findByUserId(userId));
        assertEquals("No se encontraron presupuestos para el usuario.", exception.getMessage());
    }

    @Test
    void testDeleteById_ExistingBudget() throws BudgetServiceException {

        Long budgetId = 1L;
        when(budgetRepository.existsById(budgetId)).thenReturn(true);

        budgetService.deleteById(budgetId);

        verify(budgetRepository, times(1)).deleteById(budgetId);
    }

    @Test
    void testDeleteById_BudgetNotExists() {

        Long budgetId = 1L;
        when(budgetRepository.existsById(budgetId)).thenReturn(false);

        BudgetServiceException exception = assertThrows(BudgetServiceException.class, () -> budgetService.deleteById(budgetId));
        assertEquals("El presupuesto no existe.", exception.getMessage());
    }
}