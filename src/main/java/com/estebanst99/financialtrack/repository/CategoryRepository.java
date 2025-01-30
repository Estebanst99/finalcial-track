package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByTypeAndUserEmail(String type, String userEmail);

    Optional<Category> findByNameAndUserEmail(String name, String userEmail);

    Optional<Category> findByIdAndUserEmail(Long id, String userEmail);

    List<Category> findAllByUserEmail(String userEmail);
}
