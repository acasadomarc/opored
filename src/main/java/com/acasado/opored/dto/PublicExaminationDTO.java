package com.acasado.opored.dto;

import com.acasado.opored.model.PublicExaminationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    private Integer id;

    @Schema(example = "National Police Force")
    @NotBlank
    private String name;

    @Schema(example = "Examination for the National Police Corps")
    private String description;

    @Schema(description = "Indicates if the examination is visible in the platform")
    private boolean isVisible;

    @Schema(description = "Category ID", example = "2")
    private Integer categoryId;

    @Schema(description = "Associated Bulletin Board ID", example = "5")
    private Integer bulletinBoardId;

    @Schema(description = "Associated Forum ID", example = "10")
    private Integer forumId;

    public PublicExaminationDTO(PublicExaminationEntity publicExamination) {
        setId(publicExamination.getId());
        setName(publicExamination.getName());
        setDescription(publicExamination.getDescription());
        setVisible(publicExamination.isVisible());
        setCategoryId(publicExamination.getCategory().getId());
        if(publicExamination.getBulletinBoard() != null) {
            setBulletinBoardId(publicExamination.getBulletinBoard().getId());
        }
        if(publicExamination.getForum() != null) {
            setForumId(publicExamination.getForum().getId());
        }
    }
}