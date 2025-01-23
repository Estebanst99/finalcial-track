package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.service.TransactionService;
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

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionsByUserId_Success() throws TransactionServiceException {
        Long userId = 1L;
        List<Transaction> transactions = Collections.singletonList(new Transaction());
        when(transactionService.findByUserId(userId)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void testGetTransactionsByUserId_Failure() throws TransactionServiceException {
        Long userId = 1L;
        when(transactionService.findByUserId(userId)).thenThrow(new TransactionServiceException("Error"));

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByUserId(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateTransaction_Success() throws TransactionServiceException {
        Transaction transaction = new Transaction();
        Transaction savedTransaction = new Transaction();
        when(transactionService.save(transaction)).thenReturn(savedTransaction);

        ResponseEntity<Transaction> response = transactionController.createTransaction(transaction);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedTransaction, response.getBody());
    }

    @Test
    void testCreateTransaction_Failure() throws TransactionServiceException {
        Transaction transaction = new Transaction();
        when(transactionService.save(transaction)).thenThrow(new TransactionServiceException("Error"));

        ResponseEntity<Transaction> response = transactionController.createTransaction(transaction);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}