package com.acasado.opored.util;

import com.acasado.opored.dto.QuestionDTO;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.QuestionEntity;
import com.acasado.opored.model.QuizEntity;
import lombok.NoArgsConstructor;

import java.util.Collections;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuestionFactory {

    public static QuestionDTO createValidQuestionDTO() {
        return new QuestionDTO(
                1,
                "What is the capital of Spain?",
                (byte) 1,
                20, // Test ID,
                Collections.emptySet()
        );
    }

    public static QuestionDTO createInvalidQuestionDTO() {
        return new QuestionDTO(
                null,
                null,
                (byte) 1,
                20,
                Collections.emptySet()
        );
    }

    public static QuestionEntity createValidQuestionEntity() {
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(5);

        CourseEntity course = new CourseEntity();
        course.setId(10);
        course.setProfessor(professor);

        QuizEntity test = new QuizEntity();
        test.setId(20);
        test.setCourse(course);

        QuestionEntity question = new QuestionEntity();
        question.setId(1);
        question.setStatement("What is the capital of Spain?");
        question.setPosition((byte) 1);
        question.setIsDeleted(false);
        question.setTest(test);

        return question;
    }
}