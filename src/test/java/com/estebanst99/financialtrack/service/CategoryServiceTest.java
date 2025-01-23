package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByType_ValidType() throws CategoryServiceException {
        String type = "income";
        Category category = new Category();
        category.setName("Salary");
        category.setType(type);
        when(categoryRepository.findByType(type)).thenReturn(List.of(category));

        List<Category> result = categoryService.findByType(type);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).getName());
    }

    @Test
    void testFindByType_InvalidType() {

        String type = "invalid";

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.findByType(type));
        assertEquals("El tipo de categoría debe ser 'income' o 'expense'.", exception.getMessage());
    }

    @Test
    void testSave_NewCategory() throws CategoryServiceException {

        Category category = new Category();
        category.setName("Travel");
        category.setType("expense");
        when(categoryRepository.findByType("expense")).thenReturn(List.of());
        when(categoryRepository.save(category)).thenReturn(category);

        Category savedCategory = categoryService.save(category);

        assertNotNull(savedCategory);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testSave_DuplicateCategory() {

        Category category = new Category();
        category.setName("Food");
        category.setType("expense");
        when(categoryRepository.findByType("expense")).thenReturn(List.of(category));

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.save(category));
        assertEquals("La categoría ya existe en el sistema.", exception.getMessage());
    }

    @Test
    void testValidateCategory_EmptyName() {

        Category category = new Category();
        category.setName("");
        category.setType("expense");

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.save(category));
        assertEquals("El nombre de la categoría no puede estar vacío.", exception.getMessage());
    }

    @Test
    void testDeleteById_CategoryExists() throws CategoryServiceException {

        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteById(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteById_CategoryNotExists() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.deleteById(id));
        assertEquals("La categoría con el ID proporcionado no existe.", exception.getMessage());
    }

    @Test
    void testValidateCategory_InvalidType() {

        Category category = new Category();
        category.setName("Invalid Type Category");
        category.setType("invalid"); // Tipo inválido

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> {
            categoryService.save(category);
        });
        assertEquals("El tipo de categoría debe ser 'income' o 'expense'.", exception.getMessage());
    }

    @Test
    void testValidateCategory_ValidCategory() {

        Category category = new Category();
        category.setName("Valid Category");
        category.setType("income");
        when(categoryRepository.findByType("income")).thenReturn(List.of());
        when(categoryRepository.save(category)).thenReturn(category);

        Category savedCategory = null;
        try {
            savedCategory = categoryService.save(category);
        } catch (CategoryServiceException e) {
            fail("No debería lanzar excepción para una categoría válida.");
        }

        assertNotNull(savedCategory);
        assertEquals("Valid Category", savedCategory.getName());
        assertEquals("income", savedCategory.getType());
    }
}