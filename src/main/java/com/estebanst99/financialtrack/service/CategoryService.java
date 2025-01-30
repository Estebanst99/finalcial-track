package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findByTypeAndUser(String type, String userEmail) {
        return categoryRepository.findByTypeAndUserEmail(type, userEmail);
    }

    public Optional<Category> findByNameAndUser(String name, String userEmail) {
        return categoryRepository.findByNameAndUserEmail(name, userEmail);
    }

    public Optional<Category> findByIdAndUser(Long id, String userEmail) {
        return categoryRepository.findByIdAndUserEmail(id, userEmail);
    }

    public List<Category> findAllByUser(String userEmail) {
        return categoryRepository.findAllByUserEmail(userEmail);
    }

    public boolean isCategoryDuplicate(String name, String userEmail) {
        return categoryRepository.findByNameAndUserEmail(name, userEmail).isPresent();
    }

    public boolean hasDependencies(Long categoryId) {
        //Todo Aquí se puede implementar una lógica que verifique si la categoría está asociada a transacciones o presupuestos.
        return false;
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public User findUserByEmail(String email) {
        //Todo Implementar lógica para buscar y devolver el usuario por su email.
        return new User();  // Sustituir por la implementación real
    }
}
