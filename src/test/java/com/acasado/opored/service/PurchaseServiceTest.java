package com.acasado.opored.service;

import com.acasado.opored.dto.PurchaseDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.PurchaseEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.PurchaseRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.util.PurchaseFactory;
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
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void When_GetAllPurchases_Expect_ListDTO() {
        // Arrange
        List<PurchaseEntity> entities = List.of(PurchaseFactory.createValidPurchaseEntity());
        when(purchaseRepository.findAll()).thenReturn(entities);

        // Act
        List<PurchaseDTO> result = purchaseService.getAllPurchases();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getId(), result.getFirst().getId());
    }

    // --- GetById with Security ---

    @Test
    void When_GetPurchaseById_Owner_Expect_DTO() {
        // Arrange
        int studentId = 5;
        PurchaseEntity entity = PurchaseFactory.createValidPurchaseEntity();
        // Ensure entity belongs to student 5
        entity.getStudent().setId(studentId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock that current user is the owner
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(studentId);

            when(purchaseRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act
            PurchaseDTO result = purchaseService.getPurchaseById(1);

            // Assert
            assertNotNull(result);
            assertEquals(entity.getId(), result.getId());
        }
    }

    @Test
    void Expect_StudentWithoutPermission_When_GetPurchaseById_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        PurchaseEntity entity = PurchaseFactory.createValidPurchaseEntity();
        entity.getStudent().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock that current user is NOT the owner
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);

            when(purchaseRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(StudentWithoutPermissionException.class, () -> purchaseService.getPurchaseById(1));
        }
    }

    @Test
    void Expect_Exception_When_GetPurchaseById_NotFound() {
        // Arrange
        when(purchaseRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> purchaseService.getPurchaseById(999));
    }

    // --- Create ---

    @Test
    void When_CreatePurchase_Expect_Success() {
        // Arrange
        int userId = 5;
        PurchaseDTO inputDto = PurchaseFactory.createValidPurchaseDTO();
        PurchaseEntity savedEntity = PurchaseFactory.createValidPurchaseEntity();

        // Mock dependencies existence
        when(studentRepository.findById(inputDto.getStudentId())).thenReturn(Optional.of(new StudentEntity()));
        when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(new CourseEntity()));

        when(purchaseRepository.save(any(PurchaseEntity.class))).thenReturn(savedEntity);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            // Act
            PurchaseDTO result = purchaseService.createPurchase(inputDto);

            // Assert
            assertNotNull(result);
            assertEquals(savedEntity.getId(), result.getId());
            verify(purchaseRepository).save(any(PurchaseEntity.class));
        }
    }

    @Test
    void When_ChangePurchasesOwner_Expect_UpdatedOwner() {
        StudentEntity newStudent = new StudentEntity();
        PurchaseEntity purchase = PurchaseFactory.createValidPurchaseEntity();
        java.util.Set<PurchaseEntity> purchases = new java.util.HashSet<>(List.of(purchase));

        purchaseService.changePurchasesOwner(purchases, newStudent);

        assertEquals(newStudent, purchase.getStudent());
        verify(purchaseRepository).saveAll(any());
    }
}