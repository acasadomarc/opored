package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.CategoryDTO;
import com.acasado.opored.service.CategoryService;
import com.acasado.opored.util.CategoryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest extends BaseControllerTest {

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void When_GetAllCategories_Expect_OkAndList() throws Exception {
        // Arrange
        List<CategoryDTO> dtoList = List.of(CategoryFactory.createValidCategoryDTO());
        when(categoryService.getAllCategories()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Security"));
    }

    @Test
    void When_GetCategoryById_Expect_OkAndDTO() throws Exception {
        // Arrange
        CategoryDTO dto = CategoryFactory.createValidCategoryDTO();
        when(categoryService.getCategoryById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetCategoryById_DoesNotExist() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(anyInt()))
                .thenThrow(new EntityNotFoundException("Category not found"));

        // Act
        mockMvc.perform(get("/api/categories/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreateCategory_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        CategoryDTO inputDto = CategoryFactory.createValidCategoryDTO();
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(inputDto.getName()));
    }

    @Test
    void Expect_BadRequest_When_CreateCategory_InvalidData() throws Exception {
        // Arrange
        CategoryDTO invalidDto = CategoryFactory.createInvalidCategoryDTO();

        // Act
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    void When_UpdateCategory_Expect_OkAndUpdatedDTO() throws Exception {
        // Arrange
        CategoryDTO updatedDto = CategoryFactory.createValidCategoryDTO();
        updatedDto.setName("Updated Name");

        when(categoryService.updateCategory(anyInt(), anyString(), anyString()))
                .thenReturn(updatedDto);

        // Act
        // Controller uses @RequestParam for PUT
        mockMvc.perform(put("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void When_DeleteCategory_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(anyInt());

        // Act
        mockMvc.perform(delete("/api/categories/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1);
    }
}