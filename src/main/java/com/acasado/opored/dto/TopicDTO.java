package com.acasado.opored.dto;

import com.acasado.opored.model.TopicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Detailed topic information including messages")
public class TopicDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Best strategies for the physical exam")
    @NotBlank
    private String title;

    @Schema(description = "Status of the topic", example = "OPEN")
    private String status;

    @Schema(example = "2026-06-10")
    private LocalDate publicationDate;

    @Schema(description = "ID of the forum this topic belongs to", example = "5")
    private Integer forumId;

    @Schema(description = "ID of the user who created the topic", example = "42")
    private Integer userId;

    private Set<MessageSummaryDTO> messages;

    public TopicDTO(TopicEntity topic) {
        setId(topic.getId());
        setTitle(topic.getTitle());
        setStatus(topic.getStatus().toString());
        setPublicationDate(topic.getPublicationDate());
        if (topic.getForum() != null) setForumId(topic.getForum().getId());
        if (topic.getUser() != null) setUserId(topic.getUser().getId());
        if (topic.getMessages() != null) {
            setMessages(topic.getMessages().stream()
                    .map(MessageSummaryDTO::new)
                    .collect(Collectors.toSet()));
        }
    }
}