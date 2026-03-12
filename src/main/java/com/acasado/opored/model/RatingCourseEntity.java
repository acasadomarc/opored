package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rating_courses")
public class RatingCourseEntity extends RatingEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    public RatingCourseEntity(String title, float score, StudentEntity student, CourseEntity course, String comment) {
        setTitle(title);
        setScore(score);
        setStudent(student);
        setCourse(course);
        setComment(comment);
    }
}
