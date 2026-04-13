package com.acasado.opored.service;

import com.acasado.opored.dto.*;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.security.SecurityUtils;
import com.acasado.opored.util.StudentFactory;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private JpaUserDetailsService userDetailsService;
    @Mock private TopicRepository topicRepository;
    @Mock private FollowTopicRepository followTopicRepository;
    @Mock private PublicExaminationRepository publicExaminationRepository;
    @Mock private StudentPublicExaminationRepository studentPublicExaminationRepository;
    @InjectMocks
    private StudentService studentService;

    @Test
    void When_GetAllStudents_Expect_List() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(List.of(StudentFactory.createValidStudentEntity()));

        // Act
        List<StudentSummaryDTO> result = studentService.getAllStudents();

        // Assert
        assertFalse(result.isEmpty());
    }

    @Test
    void When_GetMe_Expect_DTO() {
        // Arrange
        int userId = 1;
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(studentRepository.findById(userId)).thenReturn(Optional.of(StudentFactory.createValidStudentEntity()));

            // Act
            StudentDTO result = studentService.getMe();

            // Assert
            assertEquals(userId, result.getId());
        }
    }

    @Test
    void When_SignUp_Expect_AuthResponse() {
        // Arrange
        AuthCreateUserRequest authCreateUserRequest = StudentFactory.createValidAuthCreateUserRequest();
        AuthResponse authResponse = StudentFactory.createAuthResponse();
        when(userDetailsService.createPublicUser(any(AuthCreateUserRequest.class))).thenReturn(authResponse);

        // Act
        AuthResponse result = studentService.signUp(authCreateUserRequest);

        // Assert
        assertEquals(authResponse.getAccessToken(), result.getAccessToken());
        verify(userDetailsService).createPublicUser(argThat(req -> RoleEnum.valueOf(req.getRole()) == RoleEnum.STUDENT));
    }

    // --- Topic Logic Tests ---

    @Test
    void When_FollowTopic_Expect_Success() {
        // Arrange
        int userId = 1;
        int topicId = 100;
        StudentEntity student = StudentFactory.createValidStudentEntity();
        TopicEntity topic = StudentFactory.createTopicEntity();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(studentRepository.findById(userId)).thenReturn(Optional.of(student));
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
            when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(false);

            // Act
            studentService.followTopic(topicId);

            // Assert
            verify(followTopicRepository).save(any(FollowTopic.class));
        }
    }

    @Test
    void Expect_Exception_When_FollowTopic_AlreadyFollowed() {
        // Arrange
        int userId = 1;
        int topicId = 100;
        StudentEntity student = StudentFactory.createValidStudentEntity();
        TopicEntity topic = StudentFactory.createTopicEntity();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(studentRepository.findById(userId)).thenReturn(Optional.of(student));
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
            when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(true);

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> studentService.followTopic(topicId));
        }
    }

    @Test
    void When_UnfollowTopic_Expect_Delete() {
        // Arrange
        int userId = 1;
        int topicId = 100;

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(true);

            // Act
            studentService.unfollowTopic(topicId);

            // Assert
            verify(followTopicRepository).deleteById(any(FollowTopicId.class));
        }
    }

    // --- Public Examination Logic Tests ---

    @Test
    void When_SignUpForPublicExamination_Expect_Save() {
        // Arrange
        int userId = 1;
        int examId = 200;
        StudentEntity student = StudentFactory.createValidStudentEntity();
        PublicExaminationEntity exam = StudentFactory.createPublicExaminationEntity();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(studentRepository.findById(userId)).thenReturn(Optional.of(student));
            when(publicExaminationRepository.findById(examId)).thenReturn(Optional.of(exam));

            // Act
            studentService.signUpForPublicExamination(examId);

            // Assert
            verify(studentPublicExaminationRepository).save(any(StudentPublicExamination.class));
        }
    }

    @Test
    void Expect_Exception_When_WithdrawPublicExam_NotFound() {
        // Arrange
        int userId = 1;
        int examId = 200;

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(studentPublicExaminationRepository.existsById(any(StudentPublicExaminationId.class))).thenReturn(false);

            // Act & Assert
            assertThrows(EntityNotFoundException.class, () -> studentService.withdrawFromPublicExamination(examId));
        }
    }
}