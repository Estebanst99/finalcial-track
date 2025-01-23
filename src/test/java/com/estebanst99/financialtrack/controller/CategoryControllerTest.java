package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCategoriesByType_Success() throws CategoryServiceException {
        List<Category> categories = List.of(new Category());
        when(categoryService.findByType("income")).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.getCategoriesByType("income");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    void testGetCategoriesByType_Failure() throws CategoryServiceException {
        when(categoryService.findByType("income")).thenThrow(new CategoryServiceException("Error"));

        ResponseEntity<List<Category>> response = categoryController.getCategoriesByType("income");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetCategoriesByType_EmptyList() throws CategoryServiceException {
        when(categoryService.findByType("income")).thenReturn(List.of());

        ResponseEntity<List<Category>> response = categoryController.getCategoriesByType("income");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testCreateCategory_Success() throws CategoryServiceException {
        Category category = new Category();
        when(categoryService.save(any(Category.class))).thenReturn(category);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(category, response.getBody());
    }

    @Test
    void testCreateCategory_Failure() throws CategoryServiceException {
        when(categoryService.save(any(Category.class))).thenThrow(new CategoryServiceException("Error"));

        Category category = new Category();
        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}