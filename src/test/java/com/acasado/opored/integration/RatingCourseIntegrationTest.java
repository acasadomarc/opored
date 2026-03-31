package com.acasado.opored.integration;

import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.RatingCourseEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.RatingCourseRepository;
import com.acasado.opored.service.RatingCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RatingCourseIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RatingCourseService ratingCourseService;

    @Autowired
    private RatingCourseRepository ratingCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void When_UpdateRating_AsOwner_Expect_UpdatedValues() {
        // Arrange
        StudentEntity student = createStudent("rating-student@example.com");
        ProfessorEntity professor = createProfessor("rating-prof@example.com");

        CourseEntity course = new CourseEntity("Course", "Description", 40.0F, Set.of(), Set.of(), professor);
        course.setDiscountPercentage(0.0F);
        course.setProfessor(professor);
        course = courseRepository.save(course);

        RatingCourseEntity rating = new RatingCourseEntity("Initial", 4.0F, student, course, "Good");
        rating.setPublicationDate(LocalDate.now());
        rating = ratingCourseRepository.save(rating);

        authenticateAs(student.getId());

        // Act
        var updated = ratingCourseService.updateMyRatingCourse(rating.getId(), "Updated", 3.5F, "Updated comment");

        // Assert
        assertEquals("Updated", updated.getTitle());
        assertEquals(3.5F, updated.getScore());
    }

    @Test
    void When_DeleteRating_AsNonOwner_Expect_Exception() {
        // Arrange
        StudentEntity student = createStudent("rating-student2@example.com");
        StudentEntity otherStudent = createStudent("rating-other@example.com");
        ProfessorEntity professor = createProfessor("rating-prof2@example.com");

        CourseEntity course = new CourseEntity("Course", "Description", 40.0F, Set.of(), Set.of(), professor);
        course.setDiscountPercentage(0.0F);
        course.setProfessor(professor);
        course = courseRepository.save(course);

        RatingCourseEntity rating = new RatingCourseEntity("Initial", 4.0F, student, course, "Good");
        rating.setPublicationDate(LocalDate.now());
        rating = ratingCourseRepository.save(rating);

        authenticateAs(otherStudent.getId());

        // Act & Assert
        Integer ratingId = rating.getId();

        assertThrows(StudentWithoutPermissionException.class, () -> ratingCourseService.deleteRatingCourse(ratingId));

        // Act
        authenticateAs(student.getId(), "ROOT");

        ratingCourseService.deleteRatingCourse(rating.getId());

        // Assert
        assertTrue(ratingCourseRepository.getReferenceById(rating.getId()).getIsDeleted());
    }
}