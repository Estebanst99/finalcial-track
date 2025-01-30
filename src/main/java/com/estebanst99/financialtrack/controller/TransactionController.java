package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar transacciones.
 * Proporciona endpoints para operaciones de consulta y creación de transacciones.
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Constructor que inyecta la dependencia del servicio de transacciones.
     *
     * @param transactionService Servicio de transacciones.
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Obtiene todas las transacciones asociadas a un usuario específico.
     *
     * @param userId ID del usuario del cual se quieren obtener las transacciones.
     * @return Una respuesta HTTP que contiene la lista de transacciones del usuario.
     *         Si ocurre un error, devuelve una respuesta con código 400 (BAD_REQUEST).
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
     * Crea una nueva transacción para el usuario.
     *
     * @param transaction Objeto de tipo {@link Transaction} que representa la transacción a crear.
     * @return Una respuesta HTTP con la transacción creada y el código 201 (CREATED).
     *         Si ocurre un error, devuelve una respuesta con código 400 (BAD_REQUEST).
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
