package com.acasado.opored.util;

import com.acasado.opored.dto.RatingCourseDTO;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.RatingCourseEntity;
import com.acasado.opored.model.StudentEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RatingCourseFactory {

    public static RatingCourseDTO createValidRatingCourseDTO() {
        return new RatingCourseDTO(
                1,
                "Great Course",
                4.5f,
                LocalDate.now(),
                5,  // Student ID
                "I learned a lot",
                10  // Course ID
        );
    }

    public static RatingCourseDTO createInvalidRatingCourseDTO() {
        return new RatingCourseDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static RatingCourseEntity createValidRatingCourseEntity() {
        StudentEntity student = new StudentEntity();
        student.setId(5);

        CourseEntity course = new CourseEntity();
        course.setId(10);

        RatingCourseEntity entity = new RatingCourseEntity();
        entity.setId(1);
        entity.setTitle("Great Course");
        entity.setScore(4.5f);
        entity.setPublicationDate(LocalDate.now());
        entity.setStudent(student);
        entity.setCourse(course);
        entity.setComment("I learned a lot");
        entity.setIsDeleted(false);

        return entity;
    }

    // Helper for validation test (Student already rated)
    public static CourseEntity createCourseWithRatingByStudent(Integer studentId) {
        CourseEntity course = new CourseEntity();
        course.setId(10);

        StudentEntity student = new StudentEntity();
        student.setId(studentId);

        RatingCourseEntity existingRating = new RatingCourseEntity();
        existingRating.setStudent(student);

        Set<RatingCourseEntity> ratings = new HashSet<>();
        ratings.add(existingRating);

        course.setRatings(ratings);
        return course;
    }
}