package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.TransactionRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_ValidTransaction() throws TransactionServiceException {
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setUser(user);
        transaction.setCategory(category);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction savedTransaction = transactionService.save(transaction);

        assertNotNull(savedTransaction);
        assertEquals(100.0, savedTransaction.getAmount());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testSave_NullTransaction() {
        Transaction transaction = null;

        TransactionServiceException exception = assertThrows(TransactionServiceException.class, () -> transactionService.save(transaction));
        assertEquals("La transacción no puede ser nula.", exception.getMessage());
    }

    @Test
    void testSave_NegativeAmount() {
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setAmount(-50.0);
        transaction.setUser(user);
        transaction.setCategory(category);

        TransactionServiceException exception = assertThrows(TransactionServiceException.class, () -> transactionService.save(transaction));
        assertEquals("El monto de la transacción debe ser positivo.", exception.getMessage());
    }

    @Test
    void testFindByUserId_UserNotExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        TransactionServiceException exception = assertThrows(TransactionServiceException.class, () -> transactionService.findByUserId(userId));
        assertEquals("El usuario no existe.", exception.getMessage());
    }

    @Test
    void testFindByUserId_NoTransactionsFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        TransactionServiceException exception = assertThrows(TransactionServiceException.class, () -> transactionService.findByUserId(userId));
        assertEquals("No se encontraron transacciones para el usuario.", exception.getMessage());
    }

    @Test
    void testDeleteById_TransactionExists() throws TransactionServiceException {
        Long transactionId = 1L;
        when(transactionRepository.existsById(transactionId)).thenReturn(true);

        transactionService.deleteById(transactionId);

        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void testDeleteById_TransactionNotExists() {
        Long transactionId = 1L;
        when(transactionRepository.existsById(transactionId)).thenReturn(false);

        TransactionServiceException exception = assertThrows(TransactionServiceException.class, () -> transactionService.deleteById(transactionId));
        assertEquals("La transacción no existe.", exception.getMessage());
    }
}