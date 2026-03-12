package com.acasado.opored.dto;

import com.acasado.opored.model.PublicExaminationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Public Examination summary")
public class PublicExaminationSummaryDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "National Police Force")
    private String name;

    @Schema(example = "Examination for the National Police Corps")
    private String description;

    @Schema(description = "Associated Bulletin Board ID", example = "5")
    private Integer bulletinBoardId;

    @Schema(description = "Associated Forum ID", example = "10")
    private Integer forumId;

    public PublicExaminationSummaryDTO(PublicExaminationEntity publicExamination) {
        setId(publicExamination.getId());
        setName(publicExamination.getName());
        setDescription(publicExamination.getDescription());
        // Null checks might be needed depending on your entity mapping, kept simple as per original
        if(publicExamination.getBulletinBoard() != null) {
            setBulletinBoardId(publicExamination.getBulletinBoard().getId());
        }
        if(publicExamination.getForum() != null) {
            setForumId(publicExamination.getForum().getId());
        }
    }
}