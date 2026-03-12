package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Public Examination details")
public class PublicExaminationDTO {

    @Schema(example = "1")
    @NotNull
    private Integer id;

    @Schema(example = "National Police Force")
    @NotBlank
    private String name;

    @Schema(example = "Examination for the National Police Corps")
    private String description;

    @Schema(description = "Category ID", example = "2")
    private Integer categoryId;

    @Schema(description = "Associated Bulletin Board ID", example = "5")
    private Integer bulletinBoardId;

    @Schema(description = "Associated Forum ID", example = "10")
    private Integer forumId;
}