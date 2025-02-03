package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de transacciones financieras.
 * Proporciona endpoints para realizar operaciones CRUD sobre las transacciones del usuario autenticado.
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Constructor del controlador que inyecta el servicio de transacciones.
     *
     * @param transactionService Servicio para la gestión de transacciones.
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Recupera todas las transacciones asociadas al usuario autenticado.
     *
     * @return Una respuesta HTTP con una lista de transacciones.
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        String userEmail = getAuthenticatedUserEmail();
        List<Transaction> transactions = transactionService.findAllByUser(userEmail);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Recupera una transacción específica por su ID, si pertenece al usuario autenticado.
     *
     * @param id ID de la transacción a recuperar.
     * @return Una respuesta HTTP con la transacción encontrada o un estado 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();
        return transactionService.findByIdAndUser(id, userEmail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }

    /**
     * Crea una nueva transacción para el usuario autenticado.
     *
     * @param transaction Transacción a crear.
     * @return Una respuesta HTTP con la transacción creada.
     * @throws CategoryServiceException    Si ocurre un error relacionado con la categoría.
     * @throws TransactionServiceException Si ocurre un error relacionado con la transacción.
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) throws CategoryServiceException, TransactionServiceException {
        String userEmail = getAuthenticatedUserEmail();
        transactionService.validateAndAssignCategory(transaction, userEmail);
        Transaction savedTransaction = transactionService.save(transaction);
        return ResponseEntity.status(201).body(savedTransaction);
    }

    /**
     * Actualiza una transacción existente del usuario autenticado.
     *
     * @param id          ID de la transacción a actualizar.
     * @param transaction Datos de la transacción actualizada.
     * @return Una respuesta HTTP con la transacción actualizada.
     * @throws CategoryServiceException    Si ocurre un error relacionado con la categoría.
     * @throws TransactionServiceException Si ocurre un error relacionado con la transacción.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) throws CategoryServiceException, TransactionServiceException {
        String userEmail = getAuthenticatedUserEmail();
        Transaction updatedTransaction = transactionService.update(id, transaction, userEmail);
        return ResponseEntity.ok(updatedTransaction);
    }

    /**
     * Elimina una transacción del usuario autenticado por su ID.
     *
     * @param id ID de la transacción a eliminar.
     * @return Una respuesta HTTP con un estado 204 (sin contenido) si la eliminación es exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();
        transactionService.deleteById(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene el correo electrónico del usuario autenticado.
     *
     * @return Correo electrónico del usuario autenticado.
     */
    private String getAuthenticatedUserEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
