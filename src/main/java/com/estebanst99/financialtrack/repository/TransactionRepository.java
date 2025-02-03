package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUserEmail(String userEmail);

    Optional<Transaction> findByIdAndUserEmail(Long id, String userEmail);
}
