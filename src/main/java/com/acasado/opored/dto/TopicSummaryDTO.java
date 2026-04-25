package com.acasado.opored.dto;

import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.TopicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Brief topic information")
public class TopicSummaryDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Best strategies for the physical exam")
    private String title;

    @Schema(example = "VISIBLE")
    private StatusEnum status;

    @Schema(example = "2026-06-10")
    private LocalDate publicationDate;

    @Schema(description = "ID of the forum this topic belongs to", example = "5")
    private Integer forumId;

    @Schema(description = "ID of the user who created the topic", example = "42")
    private Integer userId;

    public TopicSummaryDTO(TopicEntity topic) {
        setId(topic.getId());
        setTitle(topic.getTitle());
        setStatus(topic.getStatus());
        setPublicationDate(topic.getPublicationDate());
        setForumId(topic.getForum().getId());
        setUserId(topic.getUser().getId());
    }
}