package com.acasado.opored.dto;

import com.acasado.opored.model.BulletinBoardEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bulletin Board details")
public class BulletinBoardDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "General Notices")
    private String name;

    @Schema(example = "Board for general public examination news")
    private String description;

    @Schema(description = "List of announcements posted on this board")
    private Set<AnnouncementDTO> announcements;

    public BulletinBoardDTO(BulletinBoardEntity bulletinBoard) {
        setId(bulletinBoard.getId());
        setName(bulletinBoard.getName());
        setDescription(bulletinBoard.getDescription());
        if (bulletinBoard.getAnnouncements() != null) {
            setAnnouncements(bulletinBoard.getAnnouncements().stream()
                    .map(AnnouncementDTO::new)
                    .collect(Collectors.toSet()));
        }
    }
}