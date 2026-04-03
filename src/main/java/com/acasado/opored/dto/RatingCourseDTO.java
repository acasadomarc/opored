package com.acasado.opored.dto;

import com.acasado.opored.model.RatingCourseEntity;
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
@Schema(description = "Details of a rating given to a course")
public class RatingCourseDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Excellent explanation")
    @NotBlank
    private String title;

    @Schema(description = "Score value (e.g., 0.0 to 5.0)", example = "4.5")
    @NotNull
    private Float score;

    @Schema(example = "2026-12-01")
    private LocalDate publicationDate;

    @Schema(description = "ID of the student who rated", example = "42")
    private Integer studentId;

    @Schema(example = "The course covered all the topics I needed for the exam.")
    private String comment;

    @Schema(description = "ID of the rated course", example = "10")
    private Integer courseId;

    public RatingCourseDTO(RatingCourseEntity ratingCourseEntity) {
        setId(ratingCourseEntity.getId());
        setTitle(ratingCourseEntity.getTitle());
        setScore(ratingCourseEntity.getScore());
        setPublicationDate(ratingCourseEntity.getPublicationDate());
        if (ratingCourseEntity.getStudent() != null) {
            setStudentId(ratingCourseEntity.getStudent().getId());
        }
        setComment(ratingCourseEntity.getComment());
        if (ratingCourseEntity.getCourse() != null) {
            setCourseId(ratingCourseEntity.getCourse().getId());
        }
    }
}