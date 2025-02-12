package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserEmail(String userEmail);

    Optional<Budget> findByUserEmailAndCategoryId(String userEmail, Long categoryId);

    @Query("SELECT b FROM Budget b WHERE b.user.email = :email AND b.category.id = :categoryId " +
            "AND ((b.startDate BETWEEN :startDate AND :endDate) OR (b.endDate BETWEEN :startDate AND :endDate))")
    List<Budget> findOverlappingBudgets(String email, Long categoryId, LocalDate startDate, LocalDate endDate);
}
