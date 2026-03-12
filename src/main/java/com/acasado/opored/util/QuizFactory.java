package com.acasado.opored.util;

import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.QuizEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuizFactory {

    public static QuizDTO createValidQuizDTO() {
        return new QuizDTO(
                1,
                "Java Basics Exam",
                "Multiple choice questions",
                3,   // allowedAttempts
                60,  // timeLimit
                50,  // scoreToPass
                100, // maxScore
                10   // courseId
        );
    }

    public static QuizDTO createInvalidQuizDTO() {
        return new QuizDTO(
                null,
                null,
                "Desc",
                -1,
                null,
                null,
                null,
                null
        );
    }

    public static QuizEntity createValidQuizEntity() {
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(5);

        CourseEntity course = new CourseEntity();
        course.setId(10);
        course.setProfessor(professor);

        QuizEntity test = new QuizEntity();
        test.setId(1);
        test.setTitle("Java Basics Exam");
        test.setDescription("Multiple choice questions");
        test.setAllowedAttempts(3);
        test.setTimeLimit(60);
        test.setScoreToPass(50);
        test.setMaxScore(100);
        test.setIsDeleted(false);
        test.setCourse(course);

        return test;
    }
}