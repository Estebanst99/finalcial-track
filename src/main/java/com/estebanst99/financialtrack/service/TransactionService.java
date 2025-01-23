package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.TransactionRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    //TODO añadir el logger

    /**
     * Constructor para inyectar los repositorios.
     */
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea una nueva transacción.
     *
     * @param transaction Transacción a guardar.
     * @return Transacción creada.
     * @throws TransactionServiceException Si la transacción es inválida.
     */
    @Transactional
    public Transaction save(Transaction transaction) throws TransactionServiceException {
        validateTransaction(transaction);

        // Verifica que el usuario exista
        if (!userRepository.existsById(transaction.getUser().getId())) {
            throw new TransactionServiceException("El usuario asociado a la transacción no existe.");
        }

        // Verifica que la categoría exista
        if (!categoryRepository.existsById(transaction.getCategory().getId())) {
            throw new TransactionServiceException("La categoría asociada a la transacción no existe.");
        }

        return transactionRepository.save(transaction);
    }

    /**
     * Recupera todas las transacciones de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de transacciones.
     * @throws TransactionServiceException Si no se encuentran transacciones o el usuario no existe.
     */
    public List<Transaction> findByUserId(Long userId) throws TransactionServiceException {
        if (!userRepository.existsById(userId)) {
            throw new TransactionServiceException("El usuario no existe.");
        }

        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        if (transactions.isEmpty()) {
            throw new TransactionServiceException("No se encontraron transacciones para el usuario.");
        }

        return transactions;
    }

    /**
     * Recupera transacciones por usuario y tipo.
     *
     * @param userId ID del usuario.
     * @param type   Tipo de transacción ('income' o 'expense').
     * @return Lista de transacciones filtradas por tipo.
     * @throws TransactionServiceException Si no se encuentran transacciones o el tipo es inválido.
     */
    public List<Transaction> findByUserIdAndType(Long userId, String type) throws TransactionServiceException {
        if (!userRepository.existsById(userId)) {
            throw new TransactionServiceException("El usuario no existe.");
        }

        if (!isValidType(type)) {
            throw new TransactionServiceException("El tipo de transacción debe ser 'income' o 'expense'.");
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndType(userId, type);
        if (transactions.isEmpty()) {
            throw new TransactionServiceException("No se encontraron transacciones para el usuario y tipo especificado.");
        }

        return transactions;
    }

    /**
     * Elimina una transacción por su ID.
     *
     * @param id ID de la transacción.
     * @throws TransactionServiceException Si la transacción no existe.
     */
    @Transactional
    public void deleteById(Long id) throws TransactionServiceException {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionServiceException("La transacción no existe.");
        }

        transactionRepository.deleteById(id);
    }

    /**
     * Valida los datos de una transacción.
     *
     * @param transaction Transacción a validar.
     * @throws TransactionServiceException Si los datos son inválidos.
     */
    private void validateTransaction(Transaction transaction) throws TransactionServiceException {
        if (transaction == null) {
            throw new TransactionServiceException("La transacción no puede ser nula.");
        }
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            throw new TransactionServiceException("El monto de la transacción debe ser positivo.");
        }
        if (transaction.getUser() == null || transaction.getUser().getId() == null) {
            throw new TransactionServiceException("La transacción debe estar asociada a un usuario válido.");
        }
        if (transaction.getCategory() == null || transaction.getCategory().getId() == null) {
            throw new TransactionServiceException("La transacción debe estar asociada a una categoría válida.");
        }
    }

    /**
     * Valida si un tipo de transacción es válido.
     *
     * @param type Tipo de transacción.
     * @return True si el tipo es válido, false en caso contrario.
     */
    private boolean isValidType(String type) {
        return "income".equalsIgnoreCase(type) || "expense".equalsIgnoreCase(type);
    }
}