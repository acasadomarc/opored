package com.acasado.opored.util;

import com.acasado.opored.dto.ContentDTO;
import com.acasado.opored.dto.CourseDTO;
import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CourseFactory {

    public static CourseDTO createValidCourseDTO() {
        return new CourseDTO(
                1,
                "Java Spring Boot",
                "Advanced Course",
                100.0f,
                0.0f,
                false,
                true,
                true,
                LocalDate.now(),
                new HashSet<>(),
                new HashSet<>(),
                0.0f,
                new ProfessorDTO()
        );
    }

    public static CourseEntity createValidCourseEntity() {
        CourseEntity course = new CourseEntity();
        course.setId(1);
        course.setName("Java Spring Boot");
        course.setDescription("Advanced Course");
        course.setPrice(100.0f);
        course.setDiscountPercentage(0.0f);
        course.setUpdateDate(LocalDate.now());
        course.setVisible(true);
        course.setDeleted(false);
        course.setContents(new HashSet<>());
        course.setRatings(new HashSet<>());
        course.setPurchases(new HashSet<>());

        // Default Professor
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(10); // Default Professor ID
        course.setProfessor(professor);


        return course;
    }

    public static ContentEntity createContentEntity() {
        ContentEntity content = new DocumentEntity();
        content.setId(1);
        content.setTitle("Intro");
        return content;
    }

    public static ContentDTO createValidContentDTO() {
        ContentDTO contentDTO = new QuizDTO(); // We use one of the subclasses
        contentDTO.setId(1);
        contentDTO.setTitle("Intro");
        return contentDTO;
    }

    // Helper: Course with purchases (for getCourseById student permission test)
    public static CourseEntity createCourseWithPurchase(Integer studentId) {
        CourseEntity course = createValidCourseEntity();

        StudentEntity student = new StudentEntity();
        student.setId(studentId);

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setStudent(student);
        purchase.setCourse(course);

        Set<PurchaseEntity> purchases = new HashSet<>();
        purchases.add(purchase);
        course.setPurchases(purchases);

        return course;
    }

    // Helper: Course with specific professor (for update permissions)
    public static CourseEntity createCourseWithProfessor(Integer professorId) {
        CourseEntity course = createValidCourseEntity();
        ProfessorEntity professor = new ProfessorEntity();
        professor.setId(professorId);
        course.setProfessor(professor);
        return course;
    }

    // Helper: Course with contents (for delete restriction)
    public static CourseEntity createCourseWithContents() {
        CourseEntity course = createValidCourseEntity();
        course.getContents().add(createContentEntity());
        return course;
    }
}