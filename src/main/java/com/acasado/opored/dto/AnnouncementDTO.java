package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Announcement details with Bulletin Info")
public class AnnouncementDTO {

    @Schema(example = "1")
    @NotNull
    private Integer id;

    @Schema(example = "New Public Examination Date")
    @NotBlank
    private String title;

    @Schema(example = "The exam has been scheduled for late October...")
    private String content;

    @Schema(example = "https://example.com/official-bulletin")
    private String relatedLinks;

    @Schema(example = "2026-05-15")
    private LocalDate publicationDate;

    @Schema(description = "ID of the associated Bulletin Board", example = "5")
    private Integer bulletinBoardId;
}