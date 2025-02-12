package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @Operation(summary = "Obtiene todas las transacciones", description = "Devuelve una lista de transacciones asociadas al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones devuelta exitosamente")
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
    @Operation(summary = "Obtiene una transacción por ID", description = "Recupera una transacción específica del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Transacción encontrada exitosamente")
    @ApiResponse(responseCode = "404", description = "Transacción no encontrada", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(
            @Parameter(description = "ID de la transacción a recuperar", required = true, example = "1")
            @PathVariable Long id) {
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
    @Operation(summary = "Crea una nueva transacción", description = "Permite crear una nueva transacción para el usuario autenticado.")
    @ApiResponse(responseCode = "201", description = "Transacción creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Error en la validación de la transacción", content = @Content)
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) throws CategoryServiceException, TransactionServiceException {
        String userEmail = getAuthenticatedUserEmail();
        transactionService.validateAndAssignCategory(transaction, userEmail);
        Transaction savedTransaction = transactionService.save(transaction);
        return ResponseEntity.status(201).body(savedTransaction);
    }

    /**
     * Recupera las transacciones filtradas por categoría y rango de fechas.
     */
    @Operation(summary = "Filtra transacciones", description = "Permite filtrar transacciones por categoría y rango de fechas.")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones filtradas devuelta exitosamente")
    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> getFilteredTransactions(
            @Parameter(description = "ID de la categoría para filtrar", example = "1") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Fecha de inicio en formato ISO (YYYY-MM-DD)", example = "2023-01-01") @RequestParam(required = false) String startDate,
            @Parameter(description = "Fecha de fin en formato ISO (YYYY-MM-DD)", example = "2023-12-31") @RequestParam(required = false) String endDate
    ) {
        String userEmail = getAuthenticatedUserEmail();
        LocalDate start = (startDate != null) ? LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE) : null;
        List<Transaction> transactions = transactionService.getTransactionsByFilters(userEmail, start, end, categoryId);
        return ResponseEntity.ok(transactions);
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
    @Operation(summary = "Actualiza una transacción", description = "Permite actualizar una transacción existente del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Transacción actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Error en la validación de la transacción", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @Parameter(description = "ID de la transacción a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Transaction transaction) throws CategoryServiceException, TransactionServiceException {
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
    @Operation(summary = "Elimina una transacción", description = "Permite eliminar una transacción por su ID.")
    @ApiResponse(responseCode = "204", description = "Transacción eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Transacción no encontrada", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "ID de la transacción a eliminar", required = true, example = "1")
            @PathVariable Long id) {
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
