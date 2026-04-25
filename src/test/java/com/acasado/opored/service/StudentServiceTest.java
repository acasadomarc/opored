package com.acasado.opored.service;

import com.acasado.opored.dto.*;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.security.SecurityUtils;
import com.acasado.opored.util.ProfessorFactory;
import com.acasado.opored.util.StudentFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Mock private RatingProfessorService ratingProfessorService;
    @Mock private RatingCourseService ratingCourseService;
    @Mock private PurchaseService purchaseService;

    @InjectMocks
    private StudentService studentService;

    private static final Integer CURRENT_USER_ID = 1;
    private static final Integer DEFAULT_DELETED_STUDENT_ID = 1;

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
    void When_GetStudentById_Expect_SummaryDTO() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

        // Act
        StudentSummaryDTO result = studentService.getStudentById(CURRENT_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getId());
    }

    @Test
    void Expect_EntityNotFoundException_When_GetStudentById_NotFound() {
        // Arrange
        when(studentRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> studentService.getStudentById(99));
    }

    @Test
    void When_GetStudentByEmail_Expect_SummaryDTO() {
        // Arrange
        String email = "student@example.com";
        StudentEntity student = StudentFactory.createValidStudentEntity();
        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));

        // Act
        StudentSummaryDTO result = studentService.getStudentByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getId());
    }

    @Test
    void Expect_EntityNotFoundException_When_GetStudentByEmail_NotFound() {
        // Arrange
        when(studentRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> studentService.getStudentByEmail("notfound@example.com"));
    }

    @Test
    void When_GetMe_Expect_DTO() {
        // Arrange
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(StudentFactory.createValidStudentEntity()));

            // Act
            StudentDTO result = studentService.getMe();

            // Assert
            assertEquals(CURRENT_USER_ID, result.getId());
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

    @Test
    void When_DisableStudent_Expect_EnabledFalseAndSaved() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        student.setEnabled(true);
        when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

        // Act
        studentService.disableStudent(CURRENT_USER_ID);

        // Assert
        assertFalse(student.isEnabled());
        verify(studentRepository).save(student);
    }

    @Test
    void When_EnableStudent_Expect_EnabledTrueAndSaved() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        student.setEnabled(false);
        when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

        // Act
        studentService.enableStudent(CURRENT_USER_ID);

        // Assert
        assertTrue(student.isEnabled());
        verify(studentRepository).save(student);
    }

    @Test
    void When_DeleteMeNoArgs_Expect_IsDeletedTrueAndSaved() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act
            studentService.deleteMe();

            // Assert
            assertTrue(student.getIsDeleted());
            assertFalse(student.isEnabled());
            verify(studentRepository).save(student);
        }
    }

    @Test
    void When_DeleteMeWithEntity_Expect_CleanUpAndTransferPurchases() {
        // Arrange
        StudentEntity studentToDelete = StudentFactory.createValidStudentEntity();
        studentToDelete.setId(2);

        TopicEntity topic = new TopicEntity(); topic.setId(10);
        studentToDelete.setTopicsFollowed(new HashSet<>(Set.of(topic)));

        PublicExaminationEntity exam = new PublicExaminationEntity(); exam.setId(20);
        studentToDelete.setPublicExaminations(new HashSet<>(Set.of(exam)));

        RatingCourseEntity ratingCourse = new RatingCourseEntity(); ratingCourse.setId(30);
        RatingProfessorEntity ratingProfessor = new RatingProfessorEntity(); ratingProfessor.setId(40);
        studentToDelete.setRatings(new HashSet<>(Set.of(ratingCourse, ratingProfessor)));

        PurchaseEntity purchase = new PurchaseEntity(); purchase.setId(50);
        studentToDelete.setPurchases(new HashSet<>(Set.of(purchase)));

        StudentEntity defaultStudent = StudentFactory.createValidStudentEntity();
        defaultStudent.setId(DEFAULT_DELETED_STUDENT_ID);

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            // Necessary because deleteMe calls methods that use getCurrentStudentUserId()
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(2);

            when(studentRepository.findById(DEFAULT_DELETED_STUDENT_ID)).thenReturn(Optional.of(defaultStudent));
            when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(true);
            when(studentPublicExaminationRepository.existsById(any(StudentPublicExaminationId.class))).thenReturn(true);

            // Act
            studentService.deleteMe(studentToDelete);

            // Assert
            verify(followTopicRepository).deleteById(any(FollowTopicId.class));
            verify(studentPublicExaminationRepository).deleteById(any(StudentPublicExaminationId.class));
            verify(ratingCourseService).deleteRatingCourse(30);
            verify(ratingProfessorService).deleteRatingProfessor(40);
            verify(purchaseService).changePurchasesOwner(studentToDelete.getPurchases(), defaultStudent);

            assertTrue(studentToDelete.getIsDeleted());
            assertFalse(studentToDelete.isEnabled());
            verify(studentRepository).save(studentToDelete);
        }
    }

    @Test
    void When_GetFollowedTopics_Expect_SetOfTopicDTO() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        TopicEntity topic = StudentFactory.createTopicEntity();
        student.setTopicsFollowed(Set.of(topic));

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act
            Set<TopicDTO> result = studentService.getFollowedTopics();

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Test
    void When_FollowTopic_Expect_Success() {
        // Arrange
        int topicId = 100;
        StudentEntity student = StudentFactory.createValidStudentEntity();
        TopicEntity topic = StudentFactory.createTopicEntity();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
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
        int topicId = 100;
        StudentEntity student = StudentFactory.createValidStudentEntity();
        TopicEntity topic = StudentFactory.createTopicEntity();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
            when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(true);

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> studentService.followTopic(topicId));
        }
    }

    @Test
    void When_UnfollowTopic_Expect_Delete() {
        // Arrange
        int topicId = 100;

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(true);

            // Act
            studentService.unfollowTopic(topicId);

            // Assert
            verify(followTopicRepository).deleteById(any(FollowTopicId.class));
        }
    }

    @Test
    void When_UnfollowDeletedTopic_Expect_DeleteById() {
        // Arrange
        int topicId = 100;
        when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(true);

        // Act
        studentService.unfollowDeletedTopic(CURRENT_USER_ID, topicId);

        // Assert
        verify(followTopicRepository).deleteById(any(FollowTopicId.class));
    }

    @Test
    void Expect_EntityNotFoundException_When_UnfollowDeletedTopic_NotExists() {
        // Arrange
        when(followTopicRepository.existsById(any(FollowTopicId.class))).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> studentService.unfollowDeletedTopic(CURRENT_USER_ID, 100));
    }

    @Test
    void When_GetEnrolledPublicExaminations_Expect_SetOfDTO() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        PublicExaminationEntity exam = StudentFactory.createPublicExaminationEntity();
        student.setPublicExaminations(Set.of(exam));

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act
            Set<PublicExaminationDTO> result = studentService.getEnrolledPublicExaminations();

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Test
    void When_SignUpForPublicExamination_Expect_Save() {
        // Arrange
        int examId = 200;
        StudentEntity student = StudentFactory.createValidStudentEntity();
        PublicExaminationEntity exam = StudentFactory.createPublicExaminationEntity();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(publicExaminationRepository.findById(examId)).thenReturn(Optional.of(exam));

            // Act
            studentService.signUpForPublicExamination(examId);

            // Assert
            verify(studentPublicExaminationRepository).save(any(StudentPublicExamination.class));
        }
    }

    @Test
    void When_WithdrawFromPublicExamination_Expect_DeleteById() {
        // Arrange
        int examId = 200;

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentPublicExaminationRepository.existsById(any(StudentPublicExaminationId.class))).thenReturn(true);

            // Act
            studentService.withdrawFromPublicExamination(examId);

            // Assert
            verify(studentPublicExaminationRepository).deleteById(any(StudentPublicExaminationId.class));
        }
    }

    @Test
    void Expect_Exception_When_WithdrawPublicExam_NotFound() {
        // Arrange
        int examId = 200;

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentPublicExaminationRepository.existsById(any(StudentPublicExaminationId.class))).thenReturn(false);

            // Act & Assert
            assertThrows(EntityNotFoundException.class, () -> studentService.withdrawFromPublicExamination(examId));
        }
    }

    @Test
    void When_GetCourses_Expect_SetOfCourseDTO() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        ProfessorEntity professor = ProfessorFactory.createValidProfessorEntity();
        PurchaseEntity purchase = new PurchaseEntity();
        CourseEntity course = new CourseEntity();
        course.setId(300);
        course.setProfessor(professor);
        purchase.setCourse(course);
        student.setPurchases(Set.of(purchase));

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act
            Set<CourseDTO> result = studentService.getCourses();

            // Assert
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void When_GetPurchases_Expect_SetOfPurchaseDTO() {
        // Arrange
        StudentEntity student = StudentFactory.createValidStudentEntity();
        PurchaseEntity purchase = new PurchaseEntity();
        CourseEntity course = new CourseEntity();
        purchase.setCourse(course); // Prevents NullPointerException due to DTO constructor mapping
        purchase.setStudent(student);
        student.setPurchases(Set.of(purchase));

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(studentRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act
            Set<PurchaseDTO> result = studentService.getPurchases();

            // Assert
            assertFalse(result.isEmpty());
        }
    }
}