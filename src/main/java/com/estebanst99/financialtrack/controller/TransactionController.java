package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Obtiene todas las transacciones de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de transacciones.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        try {
            List<Transaction> transactions = transactionService.findByUserId(userId);
            return ResponseEntity.ok(transactions);
        } catch (TransactionServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crea una nueva transacción.
     *
     * @param transaction Transacción a crear.
     * @return Transacción creada.
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction savedTransaction = transactionService.save(transaction);
            return ResponseEntity.status(201).body(savedTransaction);
        } catch (TransactionServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
