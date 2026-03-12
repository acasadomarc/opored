package com.acasado.opored.integration;

import com.acasado.opored.dto.PurchaseDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.PurchaseEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.PurchaseRepository;
import com.acasado.opored.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CoursePurchaseIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void When_GetPurchaseById_AsOwner_Expect_DTO() {
        // Arrange
        StudentEntity student = createStudent("buyer@example.com");
        ProfessorEntity professor = createProfessor("professor@example.com");

        CourseEntity course = new CourseEntity("Course", "Description", 49.99F, Set.of(), Set.of());
        course.setDiscountPercentage(0.0F);
        course.setProfessor(professor);
        course = courseRepository.save(course);

        PurchaseEntity purchase = new PurchaseEntity(LocalDate.now(), 49.99F, "Card");
        purchase.setCourse(course);
        purchase.setStudent(student);
        purchaseRepository.save(purchase);

        authenticateAs(student.getId());

        // Act
        PurchaseDTO result = purchaseService.getPurchaseById(purchase.getId());

        // Assert
        assertEquals(student.getId(), result.getStudentId());
        assertEquals(course.getId(), result.getCourseId());
    }

    @Test
    void Expect_Exception_When_GetPurchaseById_AsOtherStudent() {
        // Arrange
        StudentEntity student = createStudent("buyer2@example.com");
        StudentEntity otherStudent = createStudent("other@example.com");
        ProfessorEntity professor = createProfessor("professor2@example.com");

        CourseEntity course = new CourseEntity("Course", "Description", 49.99F, Set.of(), Set.of());
        course.setDiscountPercentage(0.0F);
        course.setProfessor(professor);
        course = courseRepository.save(course);

        PurchaseEntity purchase = new PurchaseEntity(LocalDate.now(), 49.99F, "Card");
        purchase.setCourse(course);
        purchase.setStudent(student);
        purchaseRepository.save(purchase);

        authenticateAs(otherStudent.getId());

        // Act & Assert
        Integer purchaseId = purchase.getId();

        assertThrows(StudentWithoutPermissionException.class, () -> purchaseService.getPurchaseById(purchaseId));
    }

    @Test
    void Expect_Exception_When_CreatePurchase_ForOtherStudent() {
        // Arrange
        StudentEntity student = createStudent("buyer3@example.com");
        StudentEntity otherStudent = createStudent("other2@example.com");
        ProfessorEntity professor = createProfessor("professor3@example.com");

        CourseEntity course = new CourseEntity("Course", "Description", 49.99F, Set.of(), Set.of());
        course.setDiscountPercentage(0.0F);
        course.setProfessor(professor);
        course = courseRepository.save(course);

        PurchaseDTO request = new PurchaseDTO(10, LocalDate.now(), 49.99F, "Card", course.getId(), student.getId());

        authenticateAs(otherStudent.getId());

        // Act & Assert
        assertThrows(StudentWithoutPermissionException.class, () -> purchaseService.createPurchase(request));
    }
}