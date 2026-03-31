package com.acasado.opored.service;

import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.DocumentEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.DocumentRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CourseRepository courseRepository;

    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream().map(this::convertToDocumentDTO).toList();
    }

    public DocumentDTO getDocumentById(Integer id) {
        DocumentEntity document = documentRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToDocumentDTO(document);
    }

    public DocumentDTO createDocument(DocumentDTO documentDTO) {
        Integer courseId = documentDTO.getCourseId();
        CourseEntity parentCourse = courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Parent course with id " + courseId + " not found"));
        if (!parentCourse.getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permission to add this documents to this course");
        }

        DocumentEntity document = convertToDocumentEntity(documentDTO);
        document.setCourse(parentCourse);
        DocumentEntity savedDocument = documentRepository.save(document);
        return convertToDocumentDTO(savedDocument);
    }

    public DocumentDTO updateDocument(DocumentDTO documentDTO) {
        DocumentEntity toUpdateDocument = documentRepository.findById(documentDTO.getId()).orElseThrow(() -> notFoundById(documentDTO.getId()));

        if(!toUpdateDocument.getCourse().getProfessor().getId().equals(getCurrentProfessorUserId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to update this document");
        }

        toUpdateDocument.setTitle(documentDTO.getTitle());
        toUpdateDocument.setDescription(documentDTO.getDescription());
        toUpdateDocument.setNumPages(documentDTO.getNumPages());
        toUpdateDocument.setLink(documentDTO.getLink());

        DocumentEntity updatedDocument = documentRepository.save(toUpdateDocument);
        return convertToDocumentDTO(updatedDocument);
    }

    public void deleteDocument(Integer id) {
        DocumentEntity toDeleteDocument = documentRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteDocument.getCourse().getProfessor().getId())) {
            throw new ProfessorWithoutPermissionException("You do not have permissions to delete this document");
        }

        // Logical delete
        toDeleteDocument.setIsDeleted(true);
        documentRepository.save(toDeleteDocument);
    }

    private DocumentDTO convertToDocumentDTO(DocumentEntity document) {
        return new DocumentDTO(document);
    }

    private DocumentEntity convertToDocumentEntity(DocumentDTO documentDTO) {
        return new DocumentEntity(
                documentDTO.getTitle(),
                documentDTO.getDescription(),
                documentDTO.getNumPages(),
                documentDTO.getLink());
    }

    private Integer getCurrentProfessorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserRoot() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Document with id %d not found", id));
    }
}
