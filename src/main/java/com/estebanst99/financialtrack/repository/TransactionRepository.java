package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUserEmail(String userEmail);

    Optional<Transaction> findByIdAndUserEmail(Long id, String userEmail);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category.id = :categoryId AND " +
            "t.date BETWEEN :startDate AND :endDate")
    double getTotalSpentInCategory(Long categoryId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.user.email = :userEmail " +
            "AND (:startDate IS NULL OR t.date >= :startDate) " +
            "AND (:endDate IS NULL OR t.date <= :endDate) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Transaction> findTransactionsByFilters(@Param("userEmail") String userEmail,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("categoryId") Long categoryId);
}
