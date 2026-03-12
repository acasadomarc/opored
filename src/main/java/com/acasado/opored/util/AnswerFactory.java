package com.acasado.opored.util;

import com.acasado.opored.dto.AnswerDTO;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AnswerFactory {

    public static AnswerDTO createValidAnswerDTO() {
        return new AnswerDTO(
                1,
                "The answer is 42",
                true,
                100 // Question ID
        );
    }

    public static AnswerDTO createInvalidAnswerDTO() {
        return new AnswerDTO(
                null,
                null,
                null,
                null
        );
    }

    public static AnswerEntity createValidAnswerEntity() {
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(5);

        CourseEntity course = new CourseEntity();
        course.setId(10);
        course.setProfessor(professor);

        QuizEntity test = new QuizEntity();
        test.setId(20);
        test.setCourse(course);

        QuestionEntity question = new QuestionEntity();
        question.setId(100);
        question.setTest(test);

        AnswerEntity answer = new AnswerEntity();
        answer.setId(1);
        answer.setReply("The answer is 42");
        answer.setIsCorrect(true);
        answer.setIsDeleted(false);
        answer.setQuestion(question);

        return answer;
    }
}