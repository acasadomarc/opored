package com.acasado.opored.util;

import com.acasado.opored.dto.RatingProfessorDTO;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.RatingProfessorEntity;
import com.acasado.opored.model.StudentEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RatingProfessorFactory {

    public static RatingProfessorDTO createValidRatingProfessorDTO() {
        return new RatingProfessorDTO(
                1,
                "Great Professor",
                5.0f,
                LocalDate.now(),
                5,  // Student ID
                "Very helpful",
                20  // Professor ID
        );
    }

    public static RatingProfessorDTO createInvalidRatingProfessorDTO() {
        return new RatingProfessorDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static RatingProfessorEntity createValidRatingProfessorEntity() {
        StudentEntity student = new StudentEntity();
        student.setId(5);

        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(20);

        RatingProfessorEntity entity = new RatingProfessorEntity();
        entity.setId(1);
        entity.setTitle("Great Professor");
        entity.setScore(5.0f);
        entity.setPublicationDate(LocalDate.now());
        entity.setStudent(student);
        entity.setProfessor(professor);
        entity.setComment("Very helpful");
        entity.setIsDeleted(false);

        return entity;
    }

    // Helper for validation test (Student already rated this professor)
    public static ProfessorEntity createProfessorWithRatingByStudent(Integer studentId) {
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(20);

        StudentEntity student = new StudentEntity();
        student.setId(studentId);

        RatingProfessorEntity existingRating = new RatingProfessorEntity();
        existingRating.setStudent(student);

        Set<RatingProfessorEntity> ratings = new HashSet<>();
        ratings.add(existingRating);

        professor.setRatings(ratings);
        return professor;
    }
}