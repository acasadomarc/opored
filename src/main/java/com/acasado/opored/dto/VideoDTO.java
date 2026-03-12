package com.acasado.opored.dto;

import com.acasado.opored.model.VideoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Video content details")
public class VideoDTO extends ContentDTO {
    @Schema(description = "Duration in minutes", example = "45")
    private Integer duration;

    @Schema(example = "https://vimeo.com/example-video")
    private String link;

    public VideoDTO(VideoEntity videoEntity) {
        setId(videoEntity.getId());
        setTitle(videoEntity.getTitle());
        setDescription(videoEntity.getDescription());
        setDuration(videoEntity.getDuration());
        setLink(videoEntity.getLink());
        if (videoEntity.getCourse() != null) {
            setCourseId(videoEntity.getCourse().getId());
        }
    }

    public VideoDTO(Integer id, String title, String description, Integer duration,String link, Integer courseId) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setDuration(duration);
        setLink(link);
        setCourseId(courseId);
    }
}