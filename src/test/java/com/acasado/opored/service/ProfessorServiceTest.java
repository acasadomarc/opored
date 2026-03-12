package com.acasado.opored.service;

import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.ProfessorRepository;
import com.acasado.opored.util.ProfessorFactory;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    void When_GetMe_Expect_DTO() {
        // Arrange
        int currentUserId = 1;
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            when(professorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));

            // Act
            ProfessorDTO result = professorService.getMe();

            // Assert
            assertEquals(entity.getEmail(), result.getEmail());
        }
    }

    @Test
    void When_UpdateMe_Expect_UpdatedDTO() {
        // Arrange
        int currentUserId = 1;
        String newName = "ProfessorUpdated";
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();
        UserUpdateRequest request = ProfessorFactory.createUserUpdateRequest();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            securityMock.when(() -> SecurityUtils.passwordValidation(anyString())).thenReturn(true);

            when(professorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));
            when(professorRepository.save(any(ProfessorEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            ProfessorDTO result = professorService.updateMe(request);

            // Assert
            assertEquals(newName, result.getName());
            verify(professorRepository).save(entity);
        }
    }

    @Test
    void When_DeleteProfessor_Expect_LogicalDelete() {
        // Arrange
        ProfessorEntity entity = ProfessorFactory.createValidProfessorEntity();
        when(professorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        professorService.deleteProfessor(1);

        // Assert
        assertTrue(entity.getIsDeleted());
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