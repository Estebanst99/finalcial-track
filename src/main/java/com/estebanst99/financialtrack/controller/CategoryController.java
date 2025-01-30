package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Obtiene categorías por tipo para el usuario autenticado.
     *
     * @param type Tipo de categoría ('income' o 'expense').
     * @return Lista de categorías.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getCategoriesByType(@RequestParam String type) {
        String userEmail = getAuthenticatedUserEmail();
        List<Category> categories = categoryService.findByTypeAndUser(type, userEmail);
        return ResponseEntity.ok(categories);
    }

    /**
     * Crea una nueva categoría para el usuario autenticado.
     *
     * @param category Categoría a crear.
     * @return Categoría creada o error si ya existe.
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        String userEmail = getAuthenticatedUserEmail();

        if (categoryService.isCategoryDuplicate(category.getName(), userEmail)) {
            return ResponseEntity.status(400).body("Ya existe una categoría con ese nombre.");
        }

        category.setUser(categoryService.findUserByEmail(userEmail));
        Category savedCategory = categoryService.save(category);
        return ResponseEntity.status(201).body(savedCategory);
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param id       ID de la categoría a actualizar.
     * @param category Datos de la categoría a actualizar.
     * @return Categoría actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        String userEmail = getAuthenticatedUserEmail();

        Optional<Category> existingCategory = categoryService.findByIdAndUser(id, userEmail);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(404).body("Categoría no encontrada.");
        }

        Category categoryToUpdate = existingCategory.get();
        categoryToUpdate.setName(category.getName());
        categoryToUpdate.setType(category.getType());

        Category updatedCategory = categoryService.save(categoryToUpdate);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Elimina una categoría si no tiene dependencias.
     *
     * @param id ID de la categoría a eliminar.
     * @return Respuesta exitosa o mensaje de error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();

        Optional<Category> category = categoryService.findByIdAndUser(id, userEmail);
        if (category.isEmpty()) {
            return ResponseEntity.status(404).body("Categoría no encontrada.");
        }

        if (categoryService.hasDependencies(id)) {
            return ResponseEntity.status(400).body("No se puede eliminar la categoría porque tiene dependencias.");
        }

        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todas las categorías del usuario autenticado.
     *
     * @return Lista de categorías.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        String userEmail = getAuthenticatedUserEmail();
        List<Category> categories = categoryService.findAllByUser(userEmail);
        return ResponseEntity.ok(categories);
    }

    /**
     * Busca una categoría por nombre.
     *
     * @param name Nombre de la categoría.
     * @return Categoría encontrada o error 404.
     */
    @GetMapping("/search")
    public ResponseEntity<Category> getCategoryByName(@RequestParam String name) {
        String userEmail = getAuthenticatedUserEmail();

        Optional<Category> category = categoryService.findByNameAndUser(name, userEmail);
        return category.map(ResponseEntity::ok).orElse(ResponseEntity.status(404).body(null));
    }

    private String getAuthenticatedUserEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
