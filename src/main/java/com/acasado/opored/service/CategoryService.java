package com.acasado.opored.service;

import com.acasado.opored.dto.CategoryDTO;
import com.acasado.opored.exception.RestrictedDeleteException;
import com.acasado.opored.model.CategoryEntity;
import com.acasado.opored.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::convertToCategoryDTO).toList();
    }

    public CategoryDTO getCategoryById(Integer id) {
        CategoryEntity category = categoryRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToCategoryDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        CategoryEntity category = convertToCategory(categoryDTO);
        CategoryEntity savedCategory = categoryRepository.save(category);
        return convertToCategoryDTO(savedCategory);
    }

    public CategoryDTO updateCategory(Integer id, String name, String description) {
        CategoryEntity toUpdateCategory = categoryRepository.findById(id).orElseThrow(() -> notFoundById(id));

        toUpdateCategory.setName(name);
        toUpdateCategory.setDescription(description);

        CategoryEntity updatedCategory = categoryRepository.save(toUpdateCategory);
        return convertToCategoryDTO(updatedCategory);
    }

    public void deleteCategory(Integer id) {
        CategoryEntity toDeleteCategory = categoryRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!toDeleteCategory.getPublicExaminations().isEmpty()) {
            throw new RestrictedDeleteException("You cannot delete a category with dependent public examinations. You must assign them a new category first.");
        }

        // Logical delete
        toDeleteCategory.setIsDeleted(true);
        categoryRepository.save(toDeleteCategory);
    }

    private CategoryDTO convertToCategoryDTO(CategoryEntity category) {
        return new CategoryDTO(category);
    }

    private CategoryEntity convertToCategory(CategoryDTO categoryDTO) {
        return new CategoryEntity(
                categoryDTO.getName(),
                categoryDTO.getDescription());
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Category with id %d not found", id));
    }
}
