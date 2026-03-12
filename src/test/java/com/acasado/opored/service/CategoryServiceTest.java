package com.acasado.opored.service;

import com.acasado.opored.dto.CategoryDTO;
import com.acasado.opored.exception.RestrictedDeleteException;
import com.acasado.opored.model.CategoryEntity;
import com.acasado.opored.repository.CategoryRepository;
import com.acasado.opored.util.CategoryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void When_GetAllCategories_Expect_ListDTO() {
        // Arrange
        List<CategoryEntity> entities = List.of(CategoryFactory.createValidCategoryEntity());
        when(categoryRepository.findAll()).thenReturn(entities);

        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getName(), result.getFirst().getName());
    }

    @Test
    void When_GetCategoryById_Expect_DTO() {
        // Arrange
        CategoryEntity entity = CategoryFactory.createValidCategoryEntity();
        when(categoryRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        CategoryDTO result = categoryService.getCategoryById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(999));
    }

    @Test
    void When_CreateCategory_Expect_DTO() {
        // Arrange
        CategoryDTO inputDto = CategoryFactory.createValidCategoryDTO();
        CategoryEntity savedEntity = CategoryFactory.createValidCategoryEntity();

        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(savedEntity);

        // Act
        CategoryDTO result = categoryService.createCategory(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void When_UpdateCategory_Expect_UpdatedDTO() {
        // Arrange
        CategoryEntity entity = CategoryFactory.createValidCategoryEntity();
        String newName = "New Name";

        when(categoryRepository.findById(1)).thenReturn(Optional.of(entity));
        when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        CategoryDTO result = categoryService.updateCategory(1, newName, "New Desc");

        // Assert
        assertEquals(newName, result.getName());
        verify(categoryRepository).save(entity);
    }

    @Test
    void When_DeleteCategory_Expect_LogicalDelete() {
        // Arrange
        CategoryEntity entity = CategoryFactory.createValidCategoryEntity(); // Has empty exams
        when(categoryRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        categoryService.deleteCategory(1);

        // Assert
        assertTrue(entity.getIsDeleted());
        verify(categoryRepository).save(entity);
    }

    @Test
    void Expect_RestrictedDeleteException_When_DeleteCategory_WithDependencies() {
        // Arrange
        CategoryEntity entity = CategoryFactory.createCategoryEntityWithExaminations(); // Has dependent exams
        when(categoryRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThrows(RestrictedDeleteException.class, () -> categoryService.deleteCategory(1));

        // Ensure we didn't perform the delete logic
        assertFalse(entity.getIsDeleted());
        verify(categoryRepository, never()).save(entity);
    }
}