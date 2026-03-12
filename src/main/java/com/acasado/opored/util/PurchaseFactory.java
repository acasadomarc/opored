package com.acasado.opored.util;

import com.acasado.opored.dto.PurchaseDTO;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.PurchaseEntity;
import com.acasado.opored.model.StudentEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PurchaseFactory {

    public static PurchaseDTO createValidPurchaseDTO() {
        return new PurchaseDTO(
                1,
                LocalDate.now(),
                99.99f,
                "Credit Card",
                10, // Course ID
                5   // Student ID
        );
    }

    public static PurchaseDTO createInvalidPurchaseDTO() {
        return new PurchaseDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static PurchaseEntity createValidPurchaseEntity() {
        StudentEntity student = new StudentEntity();
        student.setId(5);
        student.setEmail("student@example.com");

        CourseEntity course = new CourseEntity();
        course.setId(10);
        course.setName("Java Course");
        course.setPrice(99.99f);

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(1);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setPrice(99.99f);
        purchase.setPaymentMethod("Credit Card");
        purchase.setStudent(student);
        purchase.setCourse(course);

        return purchase;
    }
}