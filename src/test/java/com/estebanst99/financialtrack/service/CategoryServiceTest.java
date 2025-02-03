package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByTypeAndUser_ValidType() throws CategoryServiceException {
        String type = "income";
        String userEmail = "user@example.com";
        Category category = new Category();
        category.setName("Salary");
        category.setType(type);

        when(categoryRepository.findByTypeAndUserEmail(type, userEmail)).thenReturn(List.of(category));

        List<Category> result = categoryService.findByTypeAndUser(type, userEmail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).getName());
    }

    @Test
    void testFindByTypeAndUser_ExceptionThrown() {
        String type = "income";
        String userEmail = "user@example.com";

        when(categoryRepository.findByTypeAndUserEmail(type, userEmail)).thenThrow(new RuntimeException("Database error"));

        CategoryServiceException exception = assertThrows(CategoryServiceException.class,
                () -> categoryService.findByTypeAndUser(type, userEmail));

        assertEquals("Error al obtener categorías por tipo y usuario.", exception.getMessage());
    }

    @Test
    void testSave_DuplicateCategory() {
        String userEmail = "user@example.com";
        Category category = new Category();
        category.setName("Food");
        category.setUser(new User());
        category.getUser().setEmail(userEmail);

        when(categoryRepository.findByNameAndUserEmail("Food", userEmail)).thenReturn(Optional.of(category));

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.save(category));
        assertEquals("La categoría ya existe en el sistema.", exception.getMessage());
    }

    @Test
    void testDeleteById_CategoryNotExists() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.delete(categoryId));
        assertEquals("La categoría con el ID proporcionado no existe.", exception.getMessage());
    }

    @Test
    void testFindUserByEmail_UserNotExists() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        CategoryServiceException exception = assertThrows(CategoryServiceException.class, () -> categoryService.findUserByEmail(email));
        assertEquals("Usuario no encontrado con email: nonexistent@example.com", exception.getMessage());
    }
}
