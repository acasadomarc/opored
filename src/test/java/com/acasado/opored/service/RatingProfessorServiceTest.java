package com.acasado.opored.service;

import com.acasado.opored.dto.RatingProfessorDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.RatingProfessorEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.repository.RatingProfessorRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.util.RatingProfessorFactory;
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
class RatingProfessorServiceTest {

    @Mock private RatingProfessorRepository ratingProfessorRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private ProfessorRepository professorRepository;

    @InjectMocks
    private RatingProfessorService ratingProfessorService;

    // --- GetAll ---
    @Test
    void When_GetAllRatingProfessors_Expect_List() {
        when(ratingProfessorRepository.findAll()).thenReturn(List.of(RatingProfessorFactory.createValidRatingProfessorEntity()));
        List<RatingProfessorDTO> result = ratingProfessorService.getAllRatingProfessors();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        RatingProfessorEntity entity = RatingProfessorFactory.createValidRatingProfessorEntity();
        when(ratingProfessorRepository.findById(1)).thenReturn(Optional.of(entity));

        RatingProfessorDTO result = ratingProfessorService.getRatingProfessorById(1);
        assertNotNull(result);
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(ratingProfessorRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> ratingProfessorService.getRatingProfessorById(999));
    }

    // --- Create (Double Rating Check) ---
    @Test
    void When_CreateRating_Expect_Success() {
        // Arrange
        RatingProfessorDTO inputDto = RatingProfessorFactory.createValidRatingProfessorDTO();
        RatingProfessorEntity savedEntity = RatingProfessorFactory.createValidRatingProfessorEntity();
        ProfessorEntity professor = new ProfessorEntity();
        professor.setRatings(new HashSet<>()); // No existing ratings

        when(studentRepository.existsById(inputDto.getStudentId())).thenReturn(true);
        when(professorRepository.existsById(inputDto.getProfessorId())).thenReturn(true);

        // Mock Reference for validation check
        when(professorRepository.getReferenceById(inputDto.getProfessorId())).thenReturn(professor);

        // Mock References for conversion
        when(studentRepository.getReferenceById(anyInt())).thenReturn(new StudentEntity());

        when(ratingProfessorRepository.save(any(RatingProfessorEntity.class))).thenReturn(savedEntity);

        // Act
        RatingProfessorDTO result = ratingProfessorService.createRatingProfessor(inputDto);

        // Assert
        assertNotNull(result);
        verify(ratingProfessorRepository).save(any(RatingProfessorEntity.class));
    }

    @Test
    void Expect_StudentWithoutPermission_When_Create_AlreadyRated() {
        // Arrange
        RatingProfessorDTO inputDto = RatingProfessorFactory.createValidRatingProfessorDTO();
        int studentId = inputDto.getStudentId();

        // Professor ALREADY has a rating from this student
        ProfessorEntity professor = RatingProfessorFactory.createProfessorWithRatingByStudent(studentId);

        when(studentRepository.existsById(inputDto.getStudentId())).thenReturn(true);
        when(professorRepository.existsById(inputDto.getProfessorId())).thenReturn(true);
        when(professorRepository.getReferenceById(inputDto.getProfessorId())).thenReturn(professor);

        // Act & Assert
        assertThrows(StudentWithoutPermissionException.class, () -> ratingProfessorService.createRatingProfessor(inputDto));
        verify(ratingProfessorRepository, never()).save(any());
    }

    @Test
    void Expect_Exception_When_Create_StudentNotFound() {
        RatingProfessorDTO inputDto = RatingProfessorFactory.createValidRatingProfessorDTO();
        when(studentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> ratingProfessorService.createRatingProfessor(inputDto));
    }

    // --- Update By Me (Security) ---
    @Test
    void When_UpdateMyRating_Owner_Expect_Success() {
        int studentId = 5;
        RatingProfessorEntity entity = RatingProfessorFactory.createValidRatingProfessorEntity();
        // ID set in factory is 5

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(studentId);

            when(ratingProfessorRepository.findById(1)).thenReturn(Optional.of(entity));
            when(ratingProfessorRepository.save(any(RatingProfessorEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            RatingProfessorDTO result = ratingProfessorService.updateMyRatingProfessor(1, "New Title", 5.0f, "New Comment");

            assertEquals("New Title", result.getTitle());
        }
    }

    @Test
    void Expect_Exception_When_UpdateMyRating_NotOwner() {
        int intruderId = 99;
        RatingProfessorEntity entity = RatingProfessorFactory.createValidRatingProfessorEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(ratingProfessorRepository.findById(1)).thenReturn(Optional.of(entity));

            assertThrows(StudentWithoutPermissionException.class, () ->
                    ratingProfessorService.updateMyRatingProfessor(1, "Title", 5.0f, "Comment"));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteRating_Owner_Expect_LogicalDelete() {
        int studentId = 5;
        RatingProfessorEntity entity = RatingProfessorFactory.createValidRatingProfessorEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization success
            securityMock.when(() -> SecurityUtils.isProvidedUser(studentId)).thenReturn(true);

            when(ratingProfessorRepository.findById(1)).thenReturn(Optional.of(entity));

            ratingProfessorService.deleteRatingProfessor(1);

            assertTrue(entity.isDeleted());
            verify(ratingProfessorRepository).save(entity);
        }
    }
}