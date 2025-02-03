package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        // Mockear SecurityContext y Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(User.withUsername("test@example.com").password("password").roles("USER").build());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCategoriesByType_Success() throws Exception {
        Category category = new Category();
        category.setName("Salary");
        category.setType("income");

        when(categoryService.findByTypeAndUser("income", "test@example.com")).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories")
                        .param("type", "income")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Salary"))
                .andExpect(jsonPath("$[0].type").value("income"));
    }

    @Test
    void testCreateCategory_Success() throws Exception {
        Category category = new Category();
        category.setName("Travel");
        category.setType("expense");

        when(categoryService.isCategoryDuplicate("Travel", "test@example.com")).thenReturn(false);
        when(categoryService.findUserByEmail("test@example.com")).thenReturn(new com.estebanst99.financialtrack.entity.User());
        when(categoryService.save(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Travel\", \"type\": \"expense\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Travel"))
                .andExpect(jsonPath("$.type").value("expense"));
    }

    @Test
    void testCreateCategory_Duplicate() throws Exception {
        when(categoryService.isCategoryDuplicate("Travel", "test@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Travel\", \"type\": \"expense\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ya existe una categoría con ese nombre."));
    }

    @Test
    void testUpdateCategory_Success() throws Exception {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Food");
        existingCategory.setType("expense");

        when(categoryService.findByIdAndUser(1L, "test@example.com")).thenReturn(Optional.of(existingCategory));
        when(categoryService.save(any(Category.class))).thenReturn(existingCategory);

        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Groceries\", \"type\": \"expense\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    void testUpdateCategory_NotFound() throws Exception {
        when(categoryService.findByIdAndUser(1L, "test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Groceries\", \"type\": \"expense\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Categoría no encontrada."));
    }

    @Test
    void testDeleteCategory_Success() throws Exception {
        Category category = new Category();
        category.setId(1L);

        when(categoryService.findByIdAndUser(1L, "test@example.com")).thenReturn(Optional.of(category));
        when(categoryService.hasDependencies(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCategory_HasDependencies() throws Exception {
        Category category = new Category();
        category.setId(1L);

        when(categoryService.findByIdAndUser(1L, "test@example.com")).thenReturn(Optional.of(category));
        when(categoryService.hasDependencies(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No se puede eliminar la categoría porque tiene dependencias."));
    }

    @Test
    void testGetAllCategories_Success() throws Exception {
        Category category = new Category();
        category.setName("Travel");

        when(categoryService.findAllByUser("test@example.com")).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Travel"));
    }

    @Test
    void testGetCategoryByName_Success() throws Exception {
        Category category = new Category();
        category.setName("Food");

        when(categoryService.findByNameAndUser("Food", "test@example.com")).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/search")
                        .param("name", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    void testGetCategoryByName_NotFound() throws Exception {
        when(categoryService.findByNameAndUser("Nonexistent", "test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories/search")
                        .param("name", "Nonexistent"))
                .andExpect(status().isNotFound());
    }
}