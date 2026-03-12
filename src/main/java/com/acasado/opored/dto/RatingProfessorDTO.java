package com.acasado.opored.dto;

import com.acasado.opored.model.RatingProfessorEntity;
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
@Schema(description = "Details of a rating given to a professor")
public class RatingProfessorDTO {

    @Schema(example = "1")
    @NotNull
    private Integer id;

    @Schema(example = "Great mentor")
    @NotBlank
    private String title;

    @Schema(description = "Score value (e.g., 0.0 to 5.0)", example = "4.8")
    @NotNull
    private Float score;

    @Schema(example = "2026-03-15")
    private LocalDate publicationDate;

    @Schema(description = "ID of the student who rated", example = "42")
    private Integer studentId;

    @Schema(example = "Very helpful and knowledgeable professor.")
    private String comment;

    @Schema(description = "ID of the rated professor", example = "10")
    private Integer professorId;

    public RatingProfessorDTO(RatingProfessorEntity ratingProfessorEntity) {
        setId(ratingProfessorEntity.getId());
        setTitle(ratingProfessorEntity.getTitle());
        setScore(ratingProfessorEntity.getScore());
        setPublicationDate(ratingProfessorEntity.getPublicationDate());
        if (ratingProfessorEntity.getStudent() != null) {
            setStudentId(ratingProfessorEntity.getStudent().getId());
        }
        setComment(ratingProfessorEntity.getComment());
        if (ratingProfessorEntity.getProfessor() != null) {
            setProfessorId(ratingProfessorEntity.getProfessor().getId());
        }
    }
}