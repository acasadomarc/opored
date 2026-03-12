package com.acasado.opored.util;

import com.acasado.opored.dto.CategoryDTO;
import com.acasado.opored.model.CategoryEntity;
import com.acasado.opored.model.PublicExaminationEntity;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CategoryFactory {

    public static CategoryDTO createValidCategoryDTO() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1);
        dto.setName("Security");
        dto.setDescription("Police and Firefighters exams");
        dto.setPublicExaminations(new HashSet<>());
        return dto;
    }

    public static CategoryDTO createInvalidCategoryDTO() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(null); // Invalid mandatory field
        dto.setDescription("Description");
        return dto;
    }

    public static CategoryEntity createValidCategoryEntity() {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(1);
        entity.setName("Security");
        entity.setDescription("Police and Firefighters exams");
        entity.setIsDeleted(false);
        entity.setPublicExaminations(new HashSet<>());
        return entity;
    }

    public static CategoryEntity createCategoryEntityWithExaminations() {
        CategoryEntity entity = createValidCategoryEntity();
        // Mocking a child relation
        entity.setPublicExaminations(Set.of(new PublicExaminationEntity()));
        return entity;
    }
}