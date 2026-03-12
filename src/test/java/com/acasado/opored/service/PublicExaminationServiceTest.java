package com.acasado.opored.service;

import com.acasado.opored.dto.PublicExaminationDTO;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.model.CategoryEntity;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.PublicExaminationEntity;
import com.acasado.opored.repository.CategoryRepository;
import com.acasado.opored.repository.PublicExaminationRepository;
import com.acasado.opored.util.PublicExaminationFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicExaminationServiceTest {

    @Mock private PublicExaminationRepository publicExaminationRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ForumService forumService;
    @Mock private BulletinBoardService bulletinBoardService;

    @InjectMocks
    private PublicExaminationService publicExaminationService;

    // --- GetAll ---
    @Test
    void When_GetAllPublicExaminations_Expect_List() {
        when(publicExaminationRepository.findAll()).thenReturn(List.of(PublicExaminationFactory.createValidPublicExaminationEntity()));
        List<PublicExaminationDTO> result = publicExaminationService.getAllPublicExaminations();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        PublicExaminationEntity entity = PublicExaminationFactory.createValidPublicExaminationEntity();
        when(publicExaminationRepository.findById(1)).thenReturn(Optional.of(entity));

        PublicExaminationDTO result = publicExaminationService.getPublicExaminationById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(publicExaminationRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> publicExaminationService.getPublicExaminationById(999));
    }

    // --- Create (Orchestration Test) ---
    @Test
    void When_CreatePublicExamination_Expect_SuccessAndLinkedResourcesCreated() {
        // Arrange
        PublicExaminationDTO inputDto = PublicExaminationFactory.createValidPublicExaminationDTO();
        PublicExaminationEntity savedEntity = PublicExaminationFactory.createValidPublicExaminationEntity();

        when(categoryRepository.existsById(inputDto.getCategoryId())).thenReturn(true);
        when(categoryRepository.getReferenceById(anyInt())).thenReturn(new CategoryEntity());

        // Mock creation of dependent resources
        when(forumService.createForum(any(ForumEntity.class))).thenReturn(new ForumEntity());
        when(bulletinBoardService.createBulletinBoard(any(BulletinBoardEntity.class))).thenReturn(new BulletinBoardEntity());

        when(publicExaminationRepository.save(any(PublicExaminationEntity.class))).thenReturn(savedEntity);

        // Act
        PublicExaminationDTO result = publicExaminationService.createPublicExamination(inputDto);

        // Assert
        assertNotNull(result);
        // Verify dependent services called
        verify(forumService).createForum(any(ForumEntity.class));
        verify(bulletinBoardService).createBulletinBoard(any(BulletinBoardEntity.class));
        verify(publicExaminationRepository).save(any(PublicExaminationEntity.class));
    }

    @Test
    void Expect_Exception_When_Create_CategoryNotFound() {
        PublicExaminationDTO inputDto = PublicExaminationFactory.createValidPublicExaminationDTO();
        when(categoryRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> publicExaminationService.createPublicExamination(inputDto));
        verify(publicExaminationRepository, never()).save(any());
    }

    // --- Update ---
    @Test
    void When_UpdatePublicExamination_Expect_UpdatedDTO() {
        // Arrange
        PublicExaminationEntity entity = PublicExaminationFactory.createValidPublicExaminationEntity();

        when(categoryRepository.existsById(anyInt())).thenReturn(true);
        when(publicExaminationRepository.findById(1)).thenReturn(Optional.of(entity));
        when(categoryRepository.getReferenceById(anyInt())).thenReturn(new CategoryEntity());
        when(publicExaminationRepository.save(any(PublicExaminationEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        PublicExaminationDTO result = publicExaminationService.updatePublicExamination(1, "New Name", "Desc", 5);

        // Assert
        assertEquals("New Name", result.getName());
        verify(publicExaminationRepository).save(entity);
    }

    // --- Delete (Orchestration Test) ---
    @Test
    void When_DeletePublicExamination_Expect_LogicalDeleteAndCascadeResources() {
        // Arrange
        PublicExaminationEntity entity = PublicExaminationFactory.createValidPublicExaminationEntity();
        when(publicExaminationRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        publicExaminationService.deletePublicExamination(1);

        // Assert
        assertTrue(entity.getIsDeleted());
        verify(publicExaminationRepository).save(entity);

        // Verify dependent resources are deleted
        verify(forumService).deleteForum(1);
        verify(bulletinBoardService).deleteBulletinBoard(1);
    }

    // --- Get Students ---
    @Test
    void When_GetStudents_Expect_Set() {
        PublicExaminationEntity entity = PublicExaminationFactory.createPublicExaminationWithStudents();
        when(publicExaminationRepository.findById(1)).thenReturn(Optional.of(entity));

        Set<StudentSummaryDTO> students = publicExaminationService.getStudents(1);
        assertFalse(students.isEmpty());
    }
}