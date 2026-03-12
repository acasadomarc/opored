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
@Schema(description = "Details of a moderated message")
public class ModerationMessageDTO {

    @Schema(description = "ID of the moderated message", example = "10")
    private Integer messageId;

    @Schema(description = "ID of the moderator", example = "5")
    private Integer moderatorId;

    @Schema(example = "2026-08-15")
    private LocalDate moderationDate;

    @Schema(description = "Reason for moderation", example = "Spam content or inappropriate language")
    private String reason;
}