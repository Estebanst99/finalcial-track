package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.Transaction;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.exception.TransactionServiceException;
import com.estebanst99.financialtrack.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de transacciones financieras.
 * Proporciona métodos para crear, leer, actualizar y eliminar transacciones, así como
 * validar categorías asociadas.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    /**
     * Constructor del servicio de transacciones.
     *
     * @param transactionRepository Repositorio para la gestión de transacciones.
     * @param categoryService       Servicio para la gestión de categorías.
     */
    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    /**
     * Recupera todas las transacciones de un usuario específico.
     *
     * @param userEmail Correo electrónico del usuario.
     * @return Lista de transacciones asociadas al usuario.
     */
    public List<Transaction> findAllByUser(String userEmail) {
        return transactionRepository.findAllByUserEmail(userEmail);
    }

    /**
     * Busca una transacción por su ID y el usuario asociado.
     *
     * @param id        ID de la transacción.
     * @param userEmail Correo electrónico del usuario.
     * @return Un Optional con la transacción si se encuentra, o vacío si no.
     */
    public Optional<Transaction> findByIdAndUser(Long id, String userEmail) {
        return transactionRepository.findByIdAndUserEmail(id, userEmail);
    }

    /**
     * Guarda una nueva transacción en el repositorio.
     *
     * @param transaction Transacción a guardar.
     * @return La transacción guardada.
     */
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Actualiza una transacción existente.
     *
     * @param id          ID de la transacción a actualizar.
     * @param transaction Nuevos datos para la transacción.
     * @param userEmail   Correo electrónico del usuario propietario de la transacción.
     * @return La transacción actualizada.
     * @throws TransactionServiceException Si no se encuentra la transacción.
     * @throws CategoryServiceException    Si ocurre un error relacionado con la categoría.
     */
    public Transaction update(Long id, Transaction transaction, String userEmail) throws TransactionServiceException, CategoryServiceException {
        Transaction existingTransaction = findByIdAndUser(id, userEmail)
                .orElseThrow(() -> new TransactionServiceException("Transacción no encontrada."));

        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setDescription(transaction.getDescription());
        validateAndAssignCategory(transaction, userEmail);

        return transactionRepository.save(existingTransaction);
    }

    /**
     * Elimina una transacción por su ID.
     *
     * @param id        ID de la transacción a eliminar.
     * @param userEmail Correo electrónico del usuario propietario de la transacción.
     */
    public void deleteById(Long id, String userEmail) {
        transactionRepository.deleteById(id);
    }

    /**
     * Valida y asigna una categoría a una transacción.
     *
     * @param transaction Transacción a la que se le debe asignar una categoría.
     * @param userEmail   Correo electrónico del usuario propietario de la transacción.
     * @throws TransactionServiceException Si la categoría de la transacción es inválida.
     * @throws CategoryServiceException    Si ocurre un error al buscar la categoría.
     */
    public void validateAndAssignCategory(Transaction transaction, String userEmail) throws TransactionServiceException, CategoryServiceException {
        if (transaction.getCategory() == null || transaction.getCategory().getName() == null) {
            throw new TransactionServiceException("La transacción no tiene una categoría válida asignada.");
        }

        Category category = categoryService.findByNameAndUser(transaction.getCategory().getName(), userEmail)
                .orElseThrow(() -> new TransactionServiceException("Categoría no válida."));
        transaction.setCategory(category);
    }
}
