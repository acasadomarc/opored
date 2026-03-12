package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Details of a moderated topic")
public class ModerationTopicDTO {

    @Schema(description = "ID of the moderated topic", example = "15")
    private Integer topicId;

    @Schema(description = "ID of the moderator", example = "3")
    private Integer moderatorId;

    @Schema(example = "2026-09-01")
    private LocalDate moderationDate;

    @Schema(description = "Reason for moderation", example = "Off-topic discussion or duplicate content")
    private String reason;
}