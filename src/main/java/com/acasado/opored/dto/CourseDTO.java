package com.acasado.opored.dto;

import com.acasado.opored.model.ContentEntity;
import com.acasado.opored.model.CourseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Course information")
public class CourseDTO {

    @Schema(example = "1")
    @NotNull
    private Integer id;

    @Schema(example = "Advanced Java Programming")
    @NotBlank
    private String name;

    @Schema(example = "A comprehensive guide to concurrency and streams.")
    private String description;

    @Schema(description = "Final price after discount (if any)", example = "39.99")
    private Float price;

    @Schema(description = "Discount (0.0 to 100.0)", example = "10.0")
    private Float discountPercentage;

    @Schema(description = "Indicates if the course currently has a discount", example = "true")
    private Boolean hasDiscount;

    @Schema(description = "Indicas if the course can be purchased")
    private Boolean isPurchasable;

    @Schema(example = "2026-10-01")
    private LocalDate updateDate;

    @Schema(description = "Set of contents associated with the course")
    private Set<ContentDTO> contents;

    @Schema(description = "Set of ratings received for the course")
    private Set<RatingCourseDTO> ratings;

    @Schema(description = "Average score calculated from ratings", example = "4.8")
    private Float totalScore;

    @Schema(description = "Professor that created the course")
    private ProfessorDTO professor;

    public CourseDTO(CourseEntity course) {
        setId(course.getId());
        setName(course.getName());
        setDescription(course.getDescription());
        setPrice(course.getPrice(), course.getDiscountPercentage());
        setDiscountPercentage(course.getDiscountPercentage());
        setIsPurchasable(course.getIsPurchasable());
        setUpdateDate(course.getUpdateDate());
        setContents(course.getContents());
        setRatings(course.getRatings().stream().map(RatingCourseDTO::new).collect(Collectors.toSet()));
        setTotalScore();
        setProfessor(new ProfessorDTO(course.getProfessor()));
    }

    public void setPrice(Float price, Float discountPercentage) {
        if (discountPercentage == null || discountPercentage == 0.0f) {
            this.price = price;
            this.hasDiscount = false;
        } else {
            this.price = (price * (100 - discountPercentage)) / 100;
            this.hasDiscount = true;
        }
    }

    public void setTotalScore() {
        if (getRatings() == null || getRatings().isEmpty()) {
            this.totalScore = 0.0f;
        } else {
            float sumScore = (float) getRatings().stream().mapToDouble(RatingCourseDTO::getScore).sum();
            int totalVotes = getRatings().size();
            this.totalScore = sumScore / totalVotes;
        }
    }

    public void setContents(Set<ContentEntity> contents) {
        if (contents == null || contents.isEmpty()) {
            this.contents = Collections.emptySet();
            return;
        }
        this.contents = contents.stream()
                .map(ContentDTO::fromEntity)
                .collect(Collectors.toSet());
    }
}