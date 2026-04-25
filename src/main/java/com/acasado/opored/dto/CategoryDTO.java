package com.acasado.opored.dto;

import com.acasado.opored.model.CategoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Category details")
public class CategoryDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Security")
    @NotBlank
    private String name;

    @Schema(example = "Public examinations related to security forces")
    private String description;

    @Schema(description = "List of public examinations in this category")
    private Set<PublicExaminationDTO> publicExaminations;

    public CategoryDTO(CategoryEntity category) {
        setId(category.getId());
        setName(category.getName());
        setDescription(category.getDescription());
        if (category.getPublicExaminations() != null) {
            setPublicExaminations(category.getPublicExaminations().stream()
                    .map(PublicExaminationDTO::new)
                    .collect(Collectors.toSet()));
        }
    }
}