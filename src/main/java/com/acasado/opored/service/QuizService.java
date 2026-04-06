package com.acasado.opored.service;

import com.acasado.opored.dto.QuestionDTO;
import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.QuestionEntity;
import com.acasado.opored.model.QuizEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.QuizRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionService questionService;

    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream().map(this::convertToQuizDTO).toList();
    }

    public QuizDTO getQuizById(Integer id) {
        QuizEntity Quiz = quizRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToQuizDTO(Quiz);
    }

    public QuizDTO createQuiz(QuizDTO quizDTO) {
        Integer courseId = quizDTO.getCourseId();
        CourseEntity parentCourse = courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Parent course with id " + courseId + " not found"));
        if (!parentCourse.getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permission to add quizzes to this course");
        }

        QuizEntity Quiz = convertToQuizEntity(quizDTO);
        Quiz.setCourse(parentCourse);
        QuizEntity savedQuiz = quizRepository.save(Quiz);
        return convertToQuizDTO(savedQuiz);
    }

    public QuizDTO updateQuiz(QuizDTO quizDTO) {
        QuizEntity toUpdateQuiz = quizRepository.findById(quizDTO.getId()).orElseThrow(() -> notFoundById(quizDTO.getId()));

        if(!toUpdateQuiz.getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to update this quiz");
        }

        toUpdateQuiz.setTitle(quizDTO.getTitle());
        toUpdateQuiz.setDescription(quizDTO.getDescription());
        toUpdateQuiz.setTimeLimit(quizDTO.getTimeLimit());
        toUpdateQuiz.setScoreToPass(quizDTO.getScoreToPass());
        toUpdateQuiz.setMaxScore(quizDTO.getMaxScore());

        QuizEntity updatedQuiz = quizRepository.save(toUpdateQuiz);
        return convertToQuizDTO(updatedQuiz);
    }

    public void deleteQuiz(Integer id) {
        QuizEntity toDeleteQuiz = quizRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteQuiz.getCourse().getProfessor().getId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to delete this quiz");
        }

        // First, delete the associated questions
        if (!toDeleteQuiz.getQuestions().isEmpty()) {
            toDeleteQuiz.getQuestions().stream().map(QuestionEntity::getId).forEach(questionService::deleteQuestion);
        }

        // Logical delete
        toDeleteQuiz.setIsDeleted(true);
        quizRepository.save(toDeleteQuiz);
    }

    private QuizDTO convertToQuizDTO(QuizEntity Quiz) {
        return new QuizDTO(Quiz);
    }

    private QuizEntity convertToQuizEntity(QuizDTO quizDTO) {
        return new QuizEntity(
                quizDTO.getTitle(),
                quizDTO.getDescription(),
                quizDTO.getTimeLimit(),
                quizDTO.getScoreToPass(),
                quizDTO.getMaxScore(),
                setQuestionEntities(quizDTO.getQuestions()));
    }

    protected Set<QuestionEntity> setQuestionEntities(Set<QuestionDTO> questionDTOs) {
        Set<QuestionEntity> questionEntities = new HashSet<>();
        if (questionDTOs != null) {
            for (QuestionDTO questionDTO : questionDTOs) {
                questionEntities.add(new QuestionEntity(
                        questionDTO.getStatement()));
            }
        }
        return questionEntities;
    }

    private Integer getCurrentProfessorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Quiz with id %d not found", id));
    }
}
