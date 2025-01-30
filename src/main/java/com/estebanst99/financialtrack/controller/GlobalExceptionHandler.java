package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.exception.BudgetServiceException;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.exception.UserServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Captura y gestiona las excepciones comunes y personalizadas que se producen en los controladores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones genéricas no controladas.
     * Este método es llamado cuando se lanza una excepción que no está específicamente manejada en otro método.
     *
     * @param e La excepción genérica lanzada.
     * @return Una respuesta con el código de estado 500 (INTERNAL_SERVER_ERROR) y un mensaje de error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
    }

    /**
     * Maneja excepciones personalizadas relacionadas con las operaciones del negocio.
     * Captura excepciones definidas en los servicios de categorías, transacciones, presupuestos y usuarios.
     *
     * @param e La excepción personalizada lanzada.
     * @return Una respuesta con el código de estado 400 (BAD_REQUEST) y el mensaje de la excepción.
     */
    @ExceptionHandler({
            CategoryServiceException.class,
            TransactionServiceException.class,
            BudgetServiceException.class,
            UserServiceException.class
    })
    public ResponseEntity<String> handleCustomExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
