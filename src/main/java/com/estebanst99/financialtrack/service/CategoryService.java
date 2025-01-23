package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

    /**
     * Constructor para inyectar el repositorio.
     *
     * @param categoryRepository Repositorio de categorías.
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Recupera todas las categorías de un tipo específico.
     *
     * @param type Tipo de categoría ('income' o 'expense').
     * @return Lista de categorías.
     */
    public List<Category> findByType(String type) throws CategoryServiceException {
        LOGGER.debug("Buscando categoria por tipo : {}", type);
        if (!isValidType(type)) {
            throw new CategoryServiceException("El tipo de categoría debe ser 'income' o 'expense'.");
        }
        return categoryRepository.findByType(type);
    }

    /**
     * Crea una nueva categoría.
     *
     * @param category Categoría a guardar.
     * @return Categoría creada.
     */
    public Category save(Category category) throws CategoryServiceException {
        LOGGER.debug("Guardando categoria : {}", category.getName());
        validateCategory(category);
        // Verifica duplicados
        List<Category> existingCategories = categoryRepository.findByType(category.getType());
        for (Category existingCategory : existingCategories) {
            if (existingCategory.getName().equalsIgnoreCase(category.getName())) {
                throw new CategoryServiceException("La categoría ya existe en el sistema.");
            }
        }

        return categoryRepository.save(category);
    }

    /**
     * Elimina una categoría por su ID.
     *
     * @param id ID de la categoría.
     * @throws CategoryServiceException Si la categoría no existe.
     */
    public void deleteById(Long id) throws CategoryServiceException {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryServiceException("La categoría con el ID proporcionado no existe.");
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Valida si un tipo de categoría es válido.
     *
     * @param type Tipo de categoría.
     * @return True si el tipo es válido, false en caso contrario.
     */
    private boolean isValidType(String type) {
        return "income".equalsIgnoreCase(type) || "expense".equalsIgnoreCase(type);
    }

    /**
     * Valida los campos de una categoría.
     *
     * @param category Categoría a validar.
     * @throws CategoryServiceException Si los campos no son válidos.
     */
    private void validateCategory(Category category) throws CategoryServiceException {
        if (category == null) {
            throw new CategoryServiceException("La categoría no puede ser nula.");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new CategoryServiceException("El nombre de la categoría no puede estar vacío.");
        }
        if (!isValidType(category.getType())) {
            throw new CategoryServiceException("El tipo de categoría debe ser 'income' o 'expense'.");
        }
    }


}
