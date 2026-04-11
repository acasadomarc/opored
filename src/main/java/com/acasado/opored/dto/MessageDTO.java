package com.acasado.opored.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Message details")
public class MessageDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Thank you for the information regarding the exam dates.")
    @NotBlank
    private String content;

    @Schema(description = "Status of the message", example = "VISIBLE")
    private String status;

    @Schema(example = "2026-07-20")
    private Timestamp publicationDate;

    @Schema(description = "ID of the parent message (if reply)", example = "5")
    private Integer parentMessageId;

    @Schema(description = "Topic ID this message belongs to", example = "10")
    private Integer topicId;

    @Schema(description = "User ID who wrote the message", example = "42")
    private Integer userId;
}