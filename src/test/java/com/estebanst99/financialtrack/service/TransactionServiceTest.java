package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllByUser() {
        when(transactionRepository.findAllByUserEmail("user@example.com")).thenReturn(List.of(new Transaction()));

        List<Transaction> transactions = transactionService.findAllByUser("user@example.com");

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    void testFindByIdAndUser_TransactionExists() {
        Transaction transaction = new Transaction();
        when(transactionRepository.findByIdAndUserEmail(1L, "user@example.com")).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionService.findByIdAndUser(1L, "user@example.com");

        assertTrue(result.isPresent());
        assertEquals(transaction, result.get());
    }

    @Test
    void testFindByIdAndUser_TransactionNotFound() {
        when(transactionRepository.findByIdAndUserEmail(1L, "user@example.com")).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.findByIdAndUser(1L, "user@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveTransaction() {
        Transaction transaction = new Transaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction savedTransaction = transactionService.save(transaction);

        assertNotNull(savedTransaction);
    }

    @Test
    void testUpdate_TransactionExists() throws Exception {
        Transaction transaction = new Transaction();
        Category category = new Category();
        category.setName("Test Category");
        transaction.setCategory(category);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setCategory(category);

        when(transactionRepository.findByIdAndUserEmail(1L, "user@example.com")).thenReturn(Optional.of(existingTransaction));
        when(categoryService.findByNameAndUser("Test Category", "user@example.com")).thenReturn(Optional.of(category));
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);

        Transaction updatedTransaction = transactionService.update(1L, transaction, "user@example.com");

        assertNotNull(updatedTransaction);
    }

    @Test
    void testValidateAndAssignCategory_NullCategory() {
        Transaction transaction = new Transaction();

        assertThrows(TransactionServiceException.class, () -> transactionService.validateAndAssignCategory(transaction, "user@example.com"));
    }


    @Test
    void testUpdate_TransactionNotFound() {
        when(transactionRepository.findByIdAndUserEmail(1L, "user@example.com")).thenReturn(Optional.empty());

        Transaction transaction = new Transaction();

        assertThrows(TransactionServiceException.class, () -> transactionService.update(1L, transaction, "user@example.com"));
    }

    @Test
    void testValidateAndAssignCategory_CategoryExists() throws Exception {
        Category category = new Category();
        category.setName("Test Category");

        when(categoryService.findByNameAndUser(eq("Test Category"), anyString())).thenReturn(Optional.of(category));

        Transaction transaction = new Transaction();
        transaction.setCategory(category);

        transactionService.validateAndAssignCategory(transaction, "user@example.com");

        assertEquals(category, transaction.getCategory());
    }

    @Test
    void testValidateAndAssignCategory_CategoryNotFound() throws CategoryServiceException {
        when(categoryService.findByNameAndUser(anyString(), anyString())).thenReturn(Optional.empty());

        Transaction transaction = new Transaction();
        transaction.setCategory(new Category());

        assertThrows(TransactionServiceException.class, () -> transactionService.validateAndAssignCategory(transaction, "user@example.com"));
    }

    @Test
    void testDeleteTransaction() {
        doNothing().when(transactionRepository).deleteById(1L);

        transactionService.deleteById(1L, "user@example.com");

        verify(transactionRepository, times(1)).deleteById(1L);
    }
}
