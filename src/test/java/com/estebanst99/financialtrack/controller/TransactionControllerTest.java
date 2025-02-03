package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext();
    }

    private void mockSecurityContext() {
        User user = new User("user@example.com", "password", List.of());
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllTransactions() {
        when(transactionService.findAllByUser("user@example.com")).thenReturn(List.of(new Transaction()));

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetTransactionById_TransactionExists() {
        Transaction transaction = new Transaction();
        when(transactionService.findByIdAndUser(1L, "user@example.com")).thenReturn(Optional.of(transaction));

        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        assertEquals(transaction, response.getBody());
    }

    @Test
    void testGetTransactionById_TransactionNotFound() {
        when(transactionService.findByIdAndUser(1L, "user@example.com")).thenReturn(Optional.empty());

        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        assertEquals("404 NOT_FOUND", response.getStatusCode().toString());
    }

    @Test
    void testCreateTransaction() throws Exception {
        Transaction transaction = new Transaction();
        when(transactionService.save(transaction)).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.createTransaction(transaction);

        assertEquals("201 CREATED", response.getStatusCode().toString());
    }

    @Test
    void testUpdateTransaction() throws Exception {
        Transaction transaction = new Transaction();
        when(transactionService.update(1L, transaction, "user@example.com")).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.updateTransaction(1L, transaction);

        assertEquals(transaction, response.getBody());
    }

    @Test
    void testDeleteTransaction() {
        doNothing().when(transactionService).deleteById(1L, "user@example.com");

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L);

        assertEquals("204 NO_CONTENT", response.getStatusCode().toString());
    }
}
