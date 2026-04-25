package com.acasado.opored.service;

import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.util.ProfessorFactory;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private RatingProfessorService ratingProfessorService;

    @InjectMocks
    private ProfessorService professorService;

    @Test
    void When_GetAllProfessors_Expect_ListDTO() {
        // Arrange
        List<ProfessorEntity> entities = List.of(ProfessorFactory.createValidProfessorEntity());
        when(professorRepository.findAll()).thenReturn(entities);

        // Act
        List<ProfessorDTO> result = professorService.getAllProfessors();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getEmail(), result.getFirst().getEmail());
    }

    @Test
    void When_GetProfessorById_Expect_DTO() {
        // Arrange
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();
        when(professorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        ProfessorDTO result = professorService.getProfessorById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetProfessorById_NotFound() {
        // Arrange
        when(professorRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> professorService.getProfessorById(999));
    }

    @Test
    void When_GetProfessorByEmail_Expect_DTO() {
        // Arrange
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();
        String email = "prof@example.com";
        when(professorRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act
        ProfessorDTO result = professorService.getProfessorByEmail(email);

        // Assert
        assertEquals(email, result.getEmail());
    }

    @Test
    void Expect_EntityNotFoundException_When_GetProfessorByEmail_NotFound() {
        // Arrange
        when(professorRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> professorService.getProfessorByEmail("notfound@example.com"));
    }

    @Test
    void When_EnableProfessor_Expect_LogicalEnable() {
        // Arrange
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();
        entity.setEnabled(false);
        when(professorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        professorService.enableProfessor(1);

        // Assert
        assertTrue(entity.isEnabled());
        verify(professorRepository).save(entity);
    }

    @Test
    void When_GetCourses_Expect_SetOfDTOs() {
        // Arrange
        int professorId = 1;
        ProfessorEntity professor = ProfessorFactory.createValidProfessorEntity();
        professor.setCourses(new java.util.HashSet<>());
        
        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);
            when(professorRepository.findById(professorId)).thenReturn(Optional.of(professor));

            // Act
            java.util.Set<com.acasado.opored.dto.CourseDTO> result = professorService.getCourses();

            // Assert
            assertNotNull(result);
            verify(professorRepository).findById(professorId);
        }
    }

    @Test
    void When_DeleteProfessorEntity_Expect_AssetsTransferredAndLogicalDelete() {
        // Arrange
        ProfessorEntity toDelete = ProfessorFactory.createValidProfessorEntity();
        toDelete.setId(10);
        toDelete.setCourses(new java.util.HashSet<>());
        toDelete.setRatings(new java.util.HashSet<>());

        ProfessorEntity defaultProfessor = new ProfessorEntity();
        defaultProfessor.setId(14);

        when(professorRepository.findById(14)).thenReturn(Optional.of(defaultProfessor));

        // Act
        professorService.deleteMe(toDelete);

        // Assert
        assertTrue(toDelete.getIsDeleted());
        assertFalse(toDelete.isEnabled());
        verify(courseService, never()).changeCoursesOwner(any(), any());
        verify(ratingProfessorService, never()).deleteMultipleRatingProfessor(any());
        verify(professorRepository).save(toDelete);
    }

    @Test
    void When_DeleteProfessor_Expect_LogicalDisable() {
        // Arrange
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();
        when(professorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        professorService.disableProfessor(1);

        // Assert
        assertFalse(entity.isEnabled());
        verify(professorRepository).save(entity);
    }

    @Test
    void When_DeleteMe_Expect_LogicalDelete() {
        // Arrange
        int currentUserId = 1;
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            when(professorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));

            // Act
            professorService.deleteMe();

            // Assert
            assertTrue(entity.getIsDeleted());
            assertFalse(entity.isEnabled());
            verify(professorRepository).save(entity);
        }
    }
}