package com.acasado.opored.service;

import com.acasado.opored.dto.PurchaseDTO;
import com.acasado.opored.exception.StudentWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.PurchaseEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.PurchaseRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findAll().stream().map(this::convertToPurchaseDTO).toList();
    }

    public PurchaseDTO getPurchaseById(Integer id) {
        PurchaseEntity purchase = purchaseRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!purchase.getStudent().getId().equals(getCurrentStudentUserId())) {
            throw new StudentWithoutPermissionException("You do not have permission to access this purchase");
        }

        return convertToPurchaseDTO(purchase);
    }

    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        Integer courseId = purchaseDTO.getCourseId();
        Integer studentId = purchaseDTO.getStudentId();

        StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student with id " + studentId + " not found"));
        CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Course with id " + studentId + " not found"));

        if (!studentId.equals(getCurrentStudentUserId())) {
            throw new StudentWithoutPermissionException("You are not authorized to purchase this course");
        }
        PurchaseEntity purchase = convertToPurchaseEntity(purchaseDTO);
        purchase.setCourse(course);
        purchase.setStudent(student);
        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);
        return convertToPurchaseDTO(savedPurchase);
    }

    private PurchaseDTO convertToPurchaseDTO(PurchaseEntity purchase) {
        return new PurchaseDTO(purchase);
    }

    private PurchaseEntity convertToPurchaseEntity(PurchaseDTO purchaseDTO) {
        return new PurchaseEntity(
                purchaseDTO.getPurchaseDate(),
                purchaseDTO.getPrice(),
                purchaseDTO.getPaymentMethod());
    }

    private Integer getCurrentStudentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Purchase with id %d not found", id));
    }
}
