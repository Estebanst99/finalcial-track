package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserEmail(String userEmail);

    Optional<Budget> findByUserEmailAndCategoryId(String userEmail, Long categoryId);
}
