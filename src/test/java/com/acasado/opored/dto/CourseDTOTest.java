package com.acasado.opored.dto;

import com.acasado.opored.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseDTOTest {

    @Test
    void When_CreateFromEntity_Expect_CorrectMapping() {
        // Arrange
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(10);
        professor.setName("Antonio");
        professor.setSurname("Casado");

        CourseEntity course = new CourseEntity();
        course.setId(1);
        course.setName("Course Name");
        course.setDescription("Course Desc");
        course.setPrice(100.0f);
        course.setDiscountPercentage(10.0f);
        course.setVisible(true);
        course.setIsPurchasable(true);
        course.setUpdateDate(LocalDate.now());
        course.setProfessor(professor);

        // Act
        CourseDTO dto = new CourseDTO(course);

        // Assert
        assertEquals(1, dto.getId());
        assertEquals("Course Name", dto.getName());
        assertEquals(100.0f, dto.getPrice());
        assertEquals(10.0f, dto.getDiscountPercentage());
        assertTrue(dto.getHasDiscount());
        assertTrue(dto.getIsVisible());
        assertEquals(10, dto.getProfessor().getId());
    }

    @Test
    void When_SetRatings_Expect_DeletedRatingsFiltered() {
        // Arrange
        CourseDTO dto = new CourseDTO();
        
        RatingCourseEntity r1 = new RatingCourseEntity();
        r1.setId(1);
        r1.setScore(4.0f);
        r1.setDeleted(false);
        r1.setTitle("Valid");
        r1.setPublicationDate(LocalDate.now());

        RatingCourseEntity r2 = new RatingCourseEntity();
        r2.setId(2);
        r2.setScore(1.0f);
        r2.setDeleted(true); // Should be filtered
        r2.setTitle("Deleted");
        r2.setPublicationDate(LocalDate.now());

        Set<RatingCourseEntity> ratings = new LinkedHashSet<>();
        ratings.add(r1);
        ratings.add(r2);

        // Act
        dto.setRatings(ratings);

        // Assert
        assertEquals(1, dto.getRatings().size());
        assertTrue(dto.getRatings().stream().anyMatch(r -> r.getId().equals(1)));
    }

    @Test
    void Test_TotalScoreCalculation() {
        // Arrange
        CourseDTO dto = new CourseDTO();
        dto.setRatings(new HashSet<>()); // Initialize
        
        // Act & Assert (Empty)
        dto.setTotalScore();
        assertEquals(0.0f, dto.getTotalScore());

        // Act & Assert (With data)
        dto.getRatings().add(new RatingCourseDTO(1, "T1", 4.0f, null, 1, "C1", 1));
        dto.getRatings().add(new RatingCourseDTO(2, "T2", 5.0f, null, 2, "C2", 1));
        
        dto.setTotalScore();
        assertEquals(4.5f, dto.getTotalScore());
    }

    @Test
    void Test_HasDiscountLogic() {
        CourseDTO dto = new CourseDTO();
        
        dto.setDiscountPercentage(null);
        dto.setHasDiscount();
        assertFalse(dto.getHasDiscount());

        dto.setDiscountPercentage(0.0f);
        dto.setHasDiscount();
        assertFalse(dto.getHasDiscount());

        dto.setDiscountPercentage(5.0f);
        dto.setHasDiscount();
        assertTrue(dto.getHasDiscount());
    }

    @Test
    void When_SetContents_Expect_Mapping() {
        // Arrange
        CourseDTO dto = new CourseDTO();
        DocumentEntity doc = new DocumentEntity("Title", "Desc", 10, "Link");
        doc.setId(1);
        Set<ContentEntity> contents = new HashSet<>();
        contents.add(doc);

        // Act
        dto.setContents(contents);

        // Assert
        assertNotNull(dto.getContents());
        assertEquals(1, dto.getContents().size());
        assertInstanceOf(DocumentDTO.class, dto.getContents().iterator().next());
    }
}
