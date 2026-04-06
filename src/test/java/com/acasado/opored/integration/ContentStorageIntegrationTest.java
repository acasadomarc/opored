package com.acasado.opored.integration;

import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContentStorageIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void When_CreateUpdateAndDeleteDocument_Expect_PermissionChecks() {
        // Arrange
        ProfessorEntity professor = createProfessor("professor-docs@example.com");
        ProfessorEntity otherProfessor = createProfessor("other-professor@example.com");

        CourseEntity course = new CourseEntity("Course", "Description", 10.0F, Set.of(), Set.of(), professor);
        course.setDiscountPercentage(0.0F);
        course.setProfessor(professor);
        course = courseRepository.save(course);

        authenticateAs(professor.getId());

        // Act
        DocumentDTO request = new DocumentDTO(1, "Notes", "Desc", 10, "http://file", course.getId());
        DocumentDTO created = documentService.createDocument(request);

        DocumentDTO updateRequest = new DocumentDTO(created.getId(), "Notes v2", "Updated", 12, "http://file2", course.getId());
        DocumentDTO updated = documentService.updateDocument(updateRequest);

        // Assert
        assertEquals("Notes v2", updated.getTitle());
        assertEquals(12, updated.getNumPages());

        // Act
        authenticateAs(otherProfessor.getId());

        // Assert
        Integer updatedId = updated.getId();

        assertThrows(ProfessorWithoutPermissionException.class, () -> documentService.deleteDocument(updatedId));

        // Act
        authenticateAs(otherProfessor.getId(), "ROOT");

        documentService.deleteDocument(updated.getId());

        // Assert
        Integer id = updated.getId();
        assertThrows(EntityNotFoundException.class, () ->
                documentService.getDocumentById(id)
        );
    }
}