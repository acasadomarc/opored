package com.acasado.opored.service;

import com.acasado.opored.dto.ContentDTO;
import com.acasado.opored.dto.CourseDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.exception.RestrictedDeleteException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.util.CourseFactory;
import com.acasado.opored.security.SecurityUtils;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ContentService contentService;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private CourseService courseService;

    // --- GetAll ---
    @Test
    void When_GetAllCourses_Expect_List() {
        when(courseRepository.findAll()).thenReturn(List.of(CourseFactory.createValidCourseEntity()));
        List<CourseDTO> result = courseService.getAllCourses();
        assertFalse(result.isEmpty());
    }

    // --- GetById (Security Check) ---

    @Test
    void When_GetCourseById_AuthorizedStudent_Expect_DTO() {
        // Arrange
        int studentId = 5;
        int courseId = 1;
        // Mock a course that includes a purchase by this student
        CourseEntity course = CourseFactory.createCourseWithPurchase(studentId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(studentId);
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

            // Act
            CourseDTO result = courseService.getCourseById(courseId);

            // Assert
            assertNotNull(result);
            assertEquals(course.getName(), result.getName());
        }
    }

    // --- Create ---
    @Test
    void When_CreateCourse_Expect_DTO() {
        CourseDTO inputDto = CourseFactory.createValidCourseDTO();
        inputDto.getProfessor().setId(10);
        CourseEntity savedEntity = CourseFactory.createValidCourseEntity();

        when(courseRepository.save(any(CourseEntity.class))).thenReturn(savedEntity);
        when(professorRepository.findById(10)).thenReturn(Optional.of(new ProfessorEntity()));

        CourseDTO result = courseService.createCourse(inputDto);
        assertNotNull(result);
    }

    // --- Update (Security Check) ---

    @Test
    void When_UpdateCourse_OwnerProfessor_Expect_UpdatedDTO() {
        // Arrange
        int profId = 10;
        int courseId = 1;
        CourseEntity course = CourseFactory.createCourseWithProfessor(profId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(profId);
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(CourseEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            CourseDTO result = courseService.updateCourse(courseId, "New Name", "Desc", 50.0f, 10.0f, false);

            // Assert
            assertEquals("New Name", result.getName());
            assertEquals(50.0f, result.getPrice());
        }
    }

    @Test
    void Expect_ProfessorWithoutPermission_When_Update_NotOwner() {
        // Arrange
        int ownerId = 10;
        int intruderId = 99;
        CourseEntity course = CourseFactory.createCourseWithProfessor(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(courseRepository.findById(1)).thenReturn(Optional.of(course));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class,
                    () -> courseService.updateCourse(1, "Name", "Desc", 10f, 10.0f, false));
        }
    }

    // --- Add Content ---
    @Test
    void When_AddContent_OwnerProfessor_Expect_Success() {
        int profId = 10;
        CourseEntity course = CourseFactory.createCourseWithProfessor(profId);
        ContentDTO content = CourseFactory.createValidContentDTO();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(profId);
            when(courseRepository.findById(1)).thenReturn(Optional.of(course));
            when(courseRepository.save(any(CourseEntity.class))).thenReturn(course);

            CourseDTO result = courseService.addContent(1, content);
            assertNotNull(result);
        }
    }

    // --- Add Discount ---
    @Test
    void When_AddDiscount_OwnerProfessor_Expect_CalculatedPrice() {
        int profId = 10;
        CourseEntity course = CourseFactory.createCourseWithProfessor(profId);
        course.setPrice(100.0f);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(profId);
            when(courseRepository.findById(1)).thenReturn(Optional.of(course));

            Float newPrice = courseService.addDiscount(1, 0.2f); // 20% discount

            assertEquals(80.0f, newPrice);
            verify(courseRepository).save(course);
        }
    }

    // --- Delete (Restrictions) ---

    @Test
    void When_DeleteCourse_Owner_EmptyContents_Expect_LogicalDelete() {
        int profId = 10;
        CourseEntity course = CourseFactory.createCourseWithProfessor(profId);
        // Ensure contents empty
        course.getContents().clear();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock permissions check
            securityMock.when(() -> SecurityUtils.isProvidedUser(profId)).thenReturn(true);

            when(courseRepository.findById(1)).thenReturn(Optional.of(course));

            // Act
            courseService.deleteCourse(1);

            // Assert
            assertTrue(course.getIsDeleted());
            verify(courseRepository).save(course);
        }
    }

    @Test
    void Expect_RestrictedDeleteException_When_Delete_WithContents() {
        int profId = 10;
        // Course HAS contents
        CourseEntity course = CourseFactory.createCourseWithContents();
        course.setProfessor(new com.acasado.opored.model.ProfessorEntity());
        course.getProfessor().setId(profId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(() -> SecurityUtils.isProvidedUser(profId)).thenReturn(true);

            when(courseRepository.findById(1)).thenReturn(Optional.of(course));

            // Act & Assert
            assertThrows(RestrictedDeleteException.class, () -> courseService.deleteCourse(1));
            verify(courseRepository, never()).save(any());
        }
    }
}