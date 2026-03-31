package com.acasado.opored.service;

import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.QuizEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.QuizRepository;
import com.acasado.opored.security.SecurityUtils;
import com.acasado.opored.util.QuizFactory;
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
class QuizServiceTest {

    @Mock private QuizRepository quizRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private QuizService quizService;

    // --- GetAll ---
    @Test
    void When_getAllQuizzes_Expect_List() {
        when(quizRepository.findAll()).thenReturn(List.of(QuizFactory.createValidQuizEntity()));
        List<QuizDTO> result = quizService.getAllQuizzes();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        QuizEntity entity = QuizFactory.createValidQuizEntity();
        when(quizRepository.findById(1)).thenReturn(Optional.of(entity));

        QuizDTO result = quizService.getQuizById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(quizRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> quizService.getQuizById(999));
    }

    // --- Create (Security) ---
    @Test
    void When_createQuiz_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        QuizDTO inputDto = QuizFactory.createValidQuizDTO();
        CourseEntity course = new CourseEntity();
        course.setId(inputDto.getCourseId());
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(course));
            when(quizRepository.save(any(QuizEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            QuizDTO result = quizService.createQuiz(inputDto);

            // Assert
            assertNotNull(result);
            verify(quizRepository).save(any(QuizEntity.class));
        }
    }

    @Test
    void Expect_Exception_When_createQuiz_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        QuizDTO inputDto = QuizFactory.createValidQuizDTO();
        CourseEntity course = new CourseEntity();
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(course));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> quizService.createQuiz(inputDto));
            verify(quizRepository, never()).save(any());
        }
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateTest_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        QuizDTO updateDto = QuizFactory.createValidQuizDTO();
        updateDto.setTitle("New Title");

        QuizEntity entity = QuizFactory.createValidQuizEntity();
        entity.getCourse().getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(quizRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));
            when(quizRepository.save(any(QuizEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            QuizDTO result = quizService.updateQuiz(updateDto);

            // Assert
            assertEquals("New Title", result.getTitle());
        }
    }

    @Test
    void Expect_Exception_When_UpdateTest_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        QuizDTO updateDto = QuizFactory.createValidQuizDTO();
        QuizEntity entity = QuizFactory.createValidQuizEntity();
        entity.getCourse().getProfessor().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(quizRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> quizService.updateQuiz(updateDto));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteTest_Owner_Expect_LogicalDelete() {
        // Arrange
        int professorId = 5;
        QuizEntity entity = QuizFactory.createValidQuizEntity();
        entity.getCourse().getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization logic used inside isAuthorized()
            securityMock.when(() -> SecurityUtils.isProvidedUser(professorId)).thenReturn(true);

            when(quizRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act
            quizService.deleteQuiz(1);

            // Assert
            assertTrue(entity.getIsDeleted());
            verify(quizRepository).save(entity);
        }
    }
}