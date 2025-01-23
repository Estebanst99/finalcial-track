package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Obtiene categorías por tipo.
     *
     * @param type Tipo de categoría ('income' o 'expense').
     * @return Lista de categorías.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getCategoriesByType(@RequestParam String type) {
        try {
            List<Category> categories = categoryService.findByType(type);
            return ResponseEntity.ok(categories);
        } catch (CategoryServiceException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Crea una nueva categoría.
     *
     * @param category Categoría a crear.
     * @return Categoría creada.
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.save(category);
            return ResponseEntity.status(201).body(savedCategory);
        } catch (CategoryServiceException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
