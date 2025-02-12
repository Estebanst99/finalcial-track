package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Obtiene categorías por tipo", description = "Devuelve una lista de categorías de tipo 'income' o 'expense' para el usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías devuelta exitosamente")
    @ApiResponse(responseCode = "400", description = "Tipo de categoría inválido", content = @Content)
    @GetMapping
    public ResponseEntity<List<Category>> getCategoriesByType(@RequestParam String type) throws CategoryServiceException {
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
    @Operation(summary = "Crea una nueva categoría", description = "Permite crear una categoría para el usuario autenticado.")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Ya existe una categoría con ese nombre", content = @Content)
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody Category category
    ) throws CategoryServiceException {
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
    @Operation(summary = "Actualiza una categoría", description = "Permite actualizar una categoría existente.")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "ID de la categoría a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Category category
    ) throws CategoryServiceException {
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
    @Operation(summary = "Elimina una categoría", description = "Elimina una categoría por ID si no tiene dependencias.")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    @ApiResponse(responseCode = "400", description = "La categoría tiene dependencias y no puede ser eliminada", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "ID de la categoría a eliminar", required = true, example = "1")
            @PathVariable Long id
    ) throws CategoryServiceException {
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
    @Operation(summary = "Obtiene todas las categorías", description = "Devuelve una lista de todas las categorías para el usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías devuelta exitosamente")
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() throws CategoryServiceException {
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
    @Operation(summary = "Busca una categoría por nombre", description = "Busca una categoría específica por su nombre para el usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    @GetMapping("/search")
    public ResponseEntity<Category> getCategoryByName(
            @Parameter(description = "Nombre de la categoría a buscar", required = true, example = "Alimentación")
            @RequestParam String name
    ) throws CategoryServiceException {
        String userEmail = getAuthenticatedUserEmail();

        Optional<Category> category = categoryService.findByNameAndUser(name, userEmail);
        return category.map(ResponseEntity::ok).orElse(ResponseEntity.status(404).body(null));
    }

    private String getAuthenticatedUserEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
