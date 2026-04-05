package com.acasado.opored.service;

import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.DocumentEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.DocumentRepository;
import com.acasado.opored.util.DocumentFactory;
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
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private StorageService storageService;

    @InjectMocks
    private DocumentService documentService;

    // --- GetAll ---
    @Test
    void When_GetAllDocuments_Expect_List() {
        when(documentRepository.findAll()).thenReturn(List.of(DocumentFactory.createValidDocumentEntity()));
        List<DocumentDTO> result = documentService.getAllDocuments();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        DocumentEntity entity = DocumentFactory.createValidDocumentEntity();
        when(documentRepository.findById(1)).thenReturn(Optional.of(entity));

        DocumentDTO result = documentService.getDocumentById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(documentRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> documentService.getDocumentById(999));
    }

    // --- Create (Security) ---
    @Test
    void When_CreateDocument_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        DocumentDTO inputDto = DocumentFactory.createValidDocumentDTO();
        CourseEntity course = new CourseEntity();
        course.setId(inputDto.getCourseId());
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(course));
            when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            DocumentDTO result = documentService.createDocument(inputDto);

            // Assert
            assertNotNull(result);
            verify(documentRepository).save(any(DocumentEntity.class));
        }
    }

    @Test
    void Expect_Exception_When_CreateDocument_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        DocumentDTO inputDto = DocumentFactory.createValidDocumentDTO();
        CourseEntity course = new CourseEntity();
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(course));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> documentService.createDocument(inputDto));
            verify(documentRepository, never()).save(any());
        }
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateDocument_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        DocumentDTO updateDto = DocumentFactory.createValidDocumentDTO();
        updateDto.setTitle("New Title");

        DocumentEntity entity = DocumentFactory.createValidDocumentEntity();
        entity.getCourse().getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(documentRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));
            when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            DocumentDTO result = documentService.updateDocument(updateDto);

            // Assert
            assertEquals("New Title", result.getTitle());
        }
    }

    @Test
    void Expect_Exception_When_UpdateDocument_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        DocumentDTO updateDto = DocumentFactory.createValidDocumentDTO();
        DocumentEntity entity = DocumentFactory.createValidDocumentEntity();
        entity.getCourse().getProfessor().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(documentRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> documentService.updateDocument(updateDto));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteDocument_Owner_Expect_HardDelete() {
        // Arrange
        int professorId = 5;
        DocumentEntity entity = DocumentFactory.createValidDocumentEntity();
        entity.getCourse().getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization logic
            securityMock.when(() -> SecurityUtils.isProvidedUser(professorId)).thenReturn(true);

            when(documentRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act
            documentService.deleteDocument(1);

            // Assert
            verify(documentRepository).delete(entity);
        }
    }
}