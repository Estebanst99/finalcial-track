package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    /**
     * Encuentra todas las transacciones de un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Lista de transacciones del usuario.
     */
    List<Transaction> findByUserId(Long userId);

    /**
     * Encuentra transacciones por usuario y tipo (income o expense).
     *
     * @param userId ID del usuario.
     * @param type   Tipo de transacción ('income' o 'expense').
     * @return Lista de transacciones que coinciden con los criterios.
     */
    List<Transaction> findByUserIdAndType(Long userId, String type);
}
