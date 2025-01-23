package com.estebanst99.financialtrack.repository;

import com.estebanst99.financialtrack.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Repositorio para la entidad Category.
 * Maneja operaciones CRUD para categorías.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    /**
     * Encuentra todas las categorías por tipo (income o expense).
     *
     * @param type Tipo de categoría ('income' o 'expense').
     * @return Lista de categorías correspondientes al tipo.
     */
    List<Category> findByType(String type);
}
