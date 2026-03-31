package com.acasado.opored.service;

import com.acasado.opored.dto.QuestionDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.QuestionEntity;
import com.acasado.opored.model.QuizEntity;
import com.acasado.opored.repository.QuestionRepository;
import com.acasado.opored.repository.QuizRepository;
import com.acasado.opored.util.QuestionFactory;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock private QuestionRepository questionRepository;
    @Mock private QuizRepository quizRepository;

    @InjectMocks
    private QuestionService questionService;

    // --- GetAll ---
    @Test
    void When_GetAllQuestions_Expect_List() {
        when(questionRepository.findAll()).thenReturn(List.of(QuestionFactory.createValidQuestionEntity()));
        List<QuestionDTO> result = questionService.getAllQuestions();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        QuestionEntity entity = QuestionFactory.createValidQuestionEntity();
        when(questionRepository.findById(1)).thenReturn(Optional.of(entity));

        QuestionDTO result = questionService.getQuestionById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(questionRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> questionService.getQuestionById(999));
    }

    // --- Create (Deep Security Check) ---
    @Test
    void When_CreateQuestion_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        QuestionDTO inputDto = QuestionFactory.createValidQuestionDTO();

        // Build hierarchy for security check
        QuizEntity test = new QuizEntity();
        test.setId(inputDto.getTestId());
        CourseEntity course = new CourseEntity();
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(professorId);
        test.setCourse(course);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(quizRepository.findById(inputDto.getTestId())).thenReturn(Optional.of(test));
            when(questionRepository.save(any(QuestionEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            QuestionDTO result = questionService.createQuestion(inputDto);

            // Assert
            assertNotNull(result);
            verify(questionRepository).save(any(QuestionEntity.class));
        }
    }

    @Test
    void Expect_Exception_When_CreateQuestion_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        QuestionDTO inputDto = QuestionFactory.createValidQuestionDTO();

        QuizEntity test = new QuizEntity();
        CourseEntity course = new CourseEntity();
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(ownerId);
        test.setCourse(course);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(quizRepository.findById(anyInt())).thenReturn(Optional.of(test));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> questionService.createQuestion(inputDto));
            verify(questionRepository, never()).save(any());
        }
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateQuestion_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        QuestionDTO updateDto = QuestionFactory.createValidQuestionDTO();
        updateDto.setStatement("New Statement");

        QuestionEntity entity = QuestionFactory.createValidQuestionEntity();
        // Factory sets professor ID to 5

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(questionRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));
            when(questionRepository.save(any(QuestionEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            QuestionDTO result = questionService.updateQuestion(updateDto);

            // Assert
            assertEquals("New Statement", result.getStatement());
        }
    }

    @Test
    void Expect_Exception_When_UpdateQuestion_NotOwner() {
        // Arrange
        int intruderId = 99;
        QuestionDTO updateDto = QuestionFactory.createValidQuestionDTO();
        QuestionEntity entity = QuestionFactory.createValidQuestionEntity();
        // Factory sets owner to 5

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(questionRepository.findById(anyInt())).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> questionService.updateQuestion(updateDto));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteQuestion_Owner_Expect_LogicalDelete() {
        // Arrange
        int professorId = 5;
        QuestionEntity entity = QuestionFactory.createValidQuestionEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization logic
            securityMock.when(() -> SecurityUtils.isProvidedUser(professorId)).thenReturn(true);

            when(questionRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act
            questionService.deleteQuestion(1);

            // Assert
            assertTrue(entity.getIsDeleted());
            verify(questionRepository).save(entity);
        }
    }
}