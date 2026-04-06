package com.acasado.opored.service;

import com.acasado.opored.dto.QuestionDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.AnswerEntity;
import com.acasado.opored.model.QuestionEntity;
import com.acasado.opored.model.QuizEntity;
import com.acasado.opored.repository.QuestionRepository;
import com.acasado.opored.repository.QuizRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AnswerService answerService;

    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream().map(this::convertToQuestionDTO).toList();
    }

    public QuestionDTO getQuestionById(Integer id) {
        QuestionEntity question = questionRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToQuestionDTO(question);
    }

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Integer testId = questionDTO.getQuizId();
        QuizEntity parentTest = quizRepository.findById(testId).orElseThrow(() -> new EntityNotFoundException("Parent test with id " + testId + " not found"));
        if (!parentTest.getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to add questions to this test");
        }

        QuestionEntity question = convertToQuestionEntity(questionDTO);
        question.setQuiz(parentTest);
        QuestionEntity savedQuestion = questionRepository.save(question);
        return convertToQuestionDTO(savedQuestion);
    }

    public QuestionDTO updateQuestion(QuestionDTO questionDTO) {
        QuestionEntity toUpdateQuestion = questionRepository.findById(questionDTO.getId()).orElseThrow(() -> notFoundById(questionDTO.getId()));

        if (!toUpdateQuestion.getQuiz().getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to update this question");
        }

        toUpdateQuestion.setStatement(questionDTO.getStatement());

        QuestionEntity updatedQuestion = questionRepository.save(toUpdateQuestion);
        return convertToQuestionDTO(updatedQuestion);
    }

    public void deleteQuestion(Integer id) {
        QuestionEntity toDeleteQuestion = questionRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteQuestion.getQuiz().getCourse().getProfessor().getId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to delete this question");
        }

        // First, delete the associated answers
        if (!toDeleteQuestion.getAnswers().isEmpty()) {
            toDeleteQuestion.getAnswers().stream().map(AnswerEntity::getId).forEach(answerService::deleteAnswer);
        }

        // Logical delete
        toDeleteQuestion.setIsDeleted(true);
        questionRepository.save(toDeleteQuestion);
    }

    private QuestionDTO convertToQuestionDTO(QuestionEntity question) {
        return new QuestionDTO(question);
    }

    private QuestionEntity convertToQuestionEntity(QuestionDTO questionDTO) {
        return new QuestionEntity(questionDTO.getStatement());
    }

    private Integer getCurrentProfessorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Question with id %d not found", id));
    }
}