package com.acasado.opored.service;

import com.acasado.opored.dto.AnswerDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.AnswerRepository;
import com.acasado.opored.repository.QuestionRepository;
import com.acasado.opored.util.AnswerFactory;
import com.acasado.opored.util.SecurityUtils;
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
class AnswerServiceTest {

    @Mock private AnswerRepository answerRepository;
    @Mock private QuestionRepository questionRepository;

    @InjectMocks
    private AnswerService answerService;

    // --- GetAll ---
    @Test
    void When_GetAllAnswers_Expect_List() {
        when(answerRepository.findAll()).thenReturn(List.of(AnswerFactory.createValidAnswerEntity()));
        List<AnswerDTO> result = answerService.getAllAnswers();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        AnswerEntity entity = AnswerFactory.createValidAnswerEntity();
        when(answerRepository.findById(1)).thenReturn(Optional.of(entity));

        AnswerDTO result = answerService.getAnswerById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(answerRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> answerService.getAnswerById(999));
    }

    // --- Create (Security check) ---
    @Test
    void When_CreateAnswer_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        AnswerDTO inputDto = AnswerFactory.createValidAnswerDTO();

        // Build the hierarchy for permission check
        QuestionEntity question = new QuestionEntity();
        question.setId(inputDto.getQuestionId());

        QuizEntity test = new QuizEntity();
        CourseEntity course = new CourseEntity();
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(professorId);

        course.setProfessor(professor);
        test.setCourse(course);
        question.setTest(test);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(questionRepository.findById(inputDto.getQuestionId())).thenReturn(Optional.of(question));
            when(answerRepository.save(any(AnswerEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            AnswerDTO result = answerService.createAnswer(inputDto);

            // Assert
            assertNotNull(result);
            verify(answerRepository).save(any(AnswerEntity.class));
        }
    }

    @Test
    void Expect_Exception_When_CreateAnswer_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        AnswerDTO inputDto = AnswerFactory.createValidAnswerDTO();

        QuestionEntity question = new QuestionEntity();
        QuizEntity test = new QuizEntity();
        CourseEntity course = new CourseEntity();
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(ownerId);
        course.setProfessor(professor);
        test.setCourse(course);
        question.setTest(test);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(questionRepository.findById(anyInt())).thenReturn(Optional.of(question));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> answerService.createAnswer(inputDto));
            verify(answerRepository, never()).save(any());
        }
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateAnswer_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        AnswerDTO updateDto = AnswerFactory.createValidAnswerDTO();
        updateDto.setReply("New Reply");

        AnswerEntity entity = AnswerFactory.createValidAnswerEntity();
        // Factory entity has professor ID 5

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(answerRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));
            when(answerRepository.save(any(AnswerEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            AnswerDTO result = answerService.updateAnswer(updateDto);

            // Assert
            assertEquals("New Reply", result.getReply());
        }
    }

    @Test
    void Expect_Exception_When_UpdateAnswer_NotOwner() {
        // Arrange
        int intruderId = 99;
        AnswerDTO updateDto = AnswerFactory.createValidAnswerDTO();
        AnswerEntity entity = AnswerFactory.createValidAnswerEntity();
        // Factory sets owner to 5

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(answerRepository.findById(anyInt())).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> answerService.updateAnswer(updateDto));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteAnswer_Owner_Expect_LogicalDelete() {
        // Arrange
        int professorId = 5;
        AnswerEntity entity = AnswerFactory.createValidAnswerEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization logic
            securityMock.when(() -> SecurityUtils.isProvidedUser(professorId)).thenReturn(true);

            when(answerRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act
            answerService.deleteAnswer(1);

            // Assert
            assertTrue(entity.getIsDeleted());
            verify(answerRepository).save(entity);
        }
    }
}