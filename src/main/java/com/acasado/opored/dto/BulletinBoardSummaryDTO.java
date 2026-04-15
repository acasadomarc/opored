package com.acasado.opored.dto;

import com.acasado.opored.model.BulletinBoardEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bulletin Board details")
public class BulletinBoardSummaryDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "General Notices")
    private String name;

    public BulletinBoardSummaryDTO(BulletinBoardEntity bulletinBoard) {
        setId(bulletinBoard.getId());
        setName(bulletinBoard.getName());
    }
}