package com.acasado.opored.util;

import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.DocumentEntity;
import com.acasado.opored.model.ProfessorEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DocumentFactory {

    public static DocumentDTO createValidDocumentDTO() {
        return new DocumentDTO(
                1,
                "Study Guide PDF",
                "Complete guide for Chapter 1",
                50,  // numPages
                "https://docs.link/guide.pdf",
                10   // courseId
        );
    }

    public static DocumentDTO createInvalidDocumentDTO() {
        // Validation: Assuming Title is mandatory
        return new DocumentDTO(
                null,
                null,
                "Desc",
                -10, // Invalid pages
                null,
                null
        );
    }

    public static DocumentEntity createValidDocumentEntity() {
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(5);

        CourseEntity course = new CourseEntity();
        course.setId(10);
        course.setProfessor(professor);

        DocumentEntity document = new DocumentEntity();
        document.setId(1);
        document.setTitle("Study Guide PDF");
        document.setDescription("Complete guide for Chapter 1");
        document.setNumPages(50);
        document.setLink("https://docs.link/guide.pdf");
        document.setIsDeleted(false);
        document.setCourse(course);

        return document;
    }
}