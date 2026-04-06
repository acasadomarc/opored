package com.acasado.opored.service;

import com.acasado.opored.dto.RatingCourseDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.RatingCourseEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.RatingCourseRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.util.RatingCourseFactory;
import com.acasado.opored.security.SecurityUtils;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingCourseServiceTest {

    @Mock private RatingCourseRepository ratingCourseRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private RatingCourseService ratingCourseService;

    // --- GetAll ---
    @Test
    void When_GetAllRatingCourses_Expect_List() {
        when(ratingCourseRepository.findAll()).thenReturn(List.of(RatingCourseFactory.createValidRatingCourseEntity()));
        List<RatingCourseDTO> result = ratingCourseService.getAllRatingCourses();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        RatingCourseEntity entity = RatingCourseFactory.createValidRatingCourseEntity();
        when(ratingCourseRepository.findById(1)).thenReturn(Optional.of(entity));

        RatingCourseDTO result = ratingCourseService.getRatingCourseById(1);
        assertNotNull(result);
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(ratingCourseRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> ratingCourseService.getRatingCourseById(999));
    }

    // --- Create (Double Rating Check) ---
    @Test
    void When_CreateRating_Expect_Success() {
        // Arrange
        RatingCourseDTO inputDto = RatingCourseFactory.createValidRatingCourseDTO();
        RatingCourseEntity savedEntity = RatingCourseFactory.createValidRatingCourseEntity();
        CourseEntity course = new CourseEntity();
        course.setRatings(new HashSet<>()); // No existing ratings

        when(studentRepository.existsById(inputDto.getStudentId())).thenReturn(true);
        when(courseRepository.existsById(inputDto.getCourseId())).thenReturn(true);

        // Mock Reference for validation check
        when(courseRepository.getReferenceById(inputDto.getCourseId())).thenReturn(course);

        // Mock References for conversion
        when(studentRepository.getReferenceById(anyInt())).thenReturn(new StudentEntity());

        when(ratingCourseRepository.save(any(RatingCourseEntity.class))).thenReturn(savedEntity);

        // Act
        RatingCourseDTO result = ratingCourseService.createRatingCourse(inputDto);

        // Assert
        assertNotNull(result);
        verify(ratingCourseRepository).save(any(RatingCourseEntity.class));
    }

    @Test
    void Expect_StudentWithoutPermission_When_Create_AlreadyRated() {
        // Arrange
        RatingCourseDTO inputDto = RatingCourseFactory.createValidRatingCourseDTO();
        int studentId = inputDto.getStudentId();

        // Course ALREADY has a rating from this student
        CourseEntity course = RatingCourseFactory.createCourseWithRatingByStudent(studentId);

        when(studentRepository.existsById(inputDto.getStudentId())).thenReturn(true);
        when(courseRepository.existsById(inputDto.getCourseId())).thenReturn(true);
        when(courseRepository.getReferenceById(inputDto.getCourseId())).thenReturn(course);

        // Act & Assert
        assertThrows(StudentWithoutPermissionException.class, () -> ratingCourseService.createRatingCourse(inputDto));
        verify(ratingCourseRepository, never()).save(any());
    }

    @Test
    void Expect_Exception_When_Create_StudentNotFound() {
        RatingCourseDTO inputDto = RatingCourseFactory.createValidRatingCourseDTO();
        when(studentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> ratingCourseService.createRatingCourse(inputDto));
    }

    // --- Update By Me (Security) ---
    @Test
    void When_UpdateMyRating_Owner_Expect_Success() {
        int studentId = 5;
        RatingCourseEntity entity = RatingCourseFactory.createValidRatingCourseEntity();
        // ID set in factory is 5

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(studentId);

            when(ratingCourseRepository.findById(1)).thenReturn(Optional.of(entity));
            when(ratingCourseRepository.save(any(RatingCourseEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            RatingCourseDTO result = ratingCourseService.updateMyRatingCourse(1, "New Title", 5.0f, "New Comment");

            assertEquals("New Title", result.getTitle());
        }
    }

    @Test
    void Expect_Exception_When_UpdateMyRating_NotOwner() {
        int intruderId = 99;
        RatingCourseEntity entity = RatingCourseFactory.createValidRatingCourseEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(ratingCourseRepository.findById(1)).thenReturn(Optional.of(entity));

            assertThrows(StudentWithoutPermissionException.class, () ->
                    ratingCourseService.updateMyRatingCourse(1, "Title", 5.0f, "Comment"));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteRating_Owner_Expect_LogicalDelete() {
        int studentId = 5;
        RatingCourseEntity entity = RatingCourseFactory.createValidRatingCourseEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization success
            securityMock.when(() -> SecurityUtils.isProvidedUser(studentId)).thenReturn(true);

            when(ratingCourseRepository.findById(1)).thenReturn(Optional.of(entity));

            ratingCourseService.deleteRatingCourse(1);

            assertTrue(entity.isDeleted());
            verify(ratingCourseRepository).save(entity);
        }
    }
}