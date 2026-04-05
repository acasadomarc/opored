package com.acasado.opored.dto;

import com.acasado.opored.model.ContentEntity;
import com.acasado.opored.model.DocumentEntity;
import com.acasado.opored.model.QuizEntity;
import com.acasado.opored.model.VideoEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuizDTO.class, name = "QUIZ"),
        @JsonSubTypes.Type(value = DocumentDTO.class, name = "DOCUMENT"),
        @JsonSubTypes.Type(value = VideoDTO.class, name = "VIDEO")
})
@Getter
@Setter
public abstract class ContentDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Constitutional Law")
    @NotBlank
    private String title;

    @Schema(example = "Key articles from the constitution")
    private String description;

    @Schema(description = "ID of the associated course", example = "10")
    private Integer courseId;

    public ContentDTO() { }

    // Factory method
    public static ContentDTO fromEntity(ContentEntity content) {
        return switch (content) {
            case null -> null;
            case QuizEntity quizEntity -> new QuizDTO(quizEntity);
            case VideoEntity videoEntity -> new VideoDTO(videoEntity);
            case DocumentEntity documentEntity -> new DocumentDTO(documentEntity);
            default -> throw new IllegalArgumentException("Unknown ContentEntity type: " + content.getClass().getSimpleName());
        };
    }

    public static ContentEntity toEntity(ContentDTO dto) {
        switch (dto) {
            case null -> {
                return null;
            }
            case QuizDTO quizDTO -> {
                QuizEntity entity = new QuizEntity();
                entity.setId(quizDTO.getId());
                entity.setTitle(quizDTO.getTitle());
                entity.setDescription(quizDTO.getDescription());
                entity.setTimeLimit(quizDTO.getTimeLimit());
                entity.setScoreToPass(quizDTO.getScoreToPass());
                entity.setMaxScore(quizDTO.getMaxScore());
                return entity;
            }
            case VideoDTO videoDTO -> {
                VideoEntity entity = new VideoEntity();
                entity.setId(videoDTO.getId());
                entity.setTitle(videoDTO.getTitle());
                entity.setDescription(videoDTO.getDescription());
                entity.setLink(videoDTO.getLink());
                return entity;
            }
            case DocumentDTO documentDTO -> {
                DocumentEntity entity = new DocumentEntity();
                entity.setId(documentDTO.getId());
                entity.setTitle(documentDTO.getTitle());
                entity.setDescription(documentDTO.getDescription());
                entity.setLink(documentDTO.getLink());
                return entity;
            }
            default -> {
            }
        }
        throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass());
    }

}