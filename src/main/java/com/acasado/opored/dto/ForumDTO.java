package com.acasado.opored.dto;

import com.acasado.opored.model.ForumEntity;
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
@Schema(description = "Forum details and associated topics")
public class ForumDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Civil Guard Forum")
    private String name;

    @Schema(example = "Discussion space for Civil Guard examinations")
    private String description;

    @Schema(description = "List of topics belonging to this forum")
    private Set<TopicDTO> topics;

    public ForumDTO(ForumEntity forum) {
        setId(forum.getId());
        setName(forum.getName());
        setDescription(forum.getDescription());
        if (forum.getTopics() != null) {
            setTopics(forum.getTopics().stream().map(TopicDTO::new).collect(Collectors.toSet()));
        }
    }
}