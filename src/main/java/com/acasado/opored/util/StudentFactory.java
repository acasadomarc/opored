package com.acasado.opored.util;

import com.acasado.opored.dto.*;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StudentFactory {

    private static final String STUDENT_EMAIL = "student@example.com";

    public static StudentDTO createValidStudentDTO() {
        StudentDTO dto = new StudentDTO();
        dto.setId(1);
        dto.setName("Student");
        dto.setSurname("User");
        dto.setAlias("alias");
        dto.setEmail(STUDENT_EMAIL);
        dto.setPassword("newEncodedPass12A@d");
        dto.setRegistrationDate(LocalDate.now());
        dto.setEnabled(true);
        dto.setPublicExaminations(new HashSet<>());
        return dto;
    }

    public static AuthCreateUserRequest createValidAuthCreateUserRequest() {
        AuthCreateUserRequest request = new AuthCreateUserRequest();
        StudentDTO dto = createValidStudentDTO();
        request.setName(dto.getName());
        request.setSurname(dto.getSurname());
        request.setAlias(dto.getAlias());
        request.setEmail(dto.getEmail());
        request.setPassword(dto.getPassword());
        request.setRole("STUDENT");
        return request;

    }

    public static StudentSummaryDTO createValidStudentSummaryDTO() {
        StudentEntity entity = createValidStudentEntity();
        return new StudentSummaryDTO(entity);
    }

    public static StudentEntity createValidStudentEntity() {
        StudentEntity entity = new StudentEntity();
        entity.setId(1);
        entity.setName("Student");
        entity.setSurname("User");
        entity.setEmail(STUDENT_EMAIL);
        entity.setPassword("encodedPass");
        entity.setRegistrationDate(LocalDate.now());
        entity.setEnabled(true);
        entity.setIsDeleted(false);
        entity.setTopicsFollowed(new HashSet<>());
        entity.setPublicExaminations(new HashSet<>());
        entity.setPurchases(new HashSet<>());
        return entity;
    }

    public static AuthResponse createAuthResponse() {
        return new AuthResponse(STUDENT_EMAIL, "Success", "jwt-token","refresh-token", 200);
    }

    public static Set<TopicDTO> createTopicDTOSet() {
        return Set.of(new TopicDTO());
    }

    public static Set<CourseDTO> createCourseDTOSet() {
        return Set.of(new CourseDTO());
    }

    public static Set<PurchaseDTO> createPurchaseDTOSet() {
        return Set.of(new PurchaseDTO());
    }

    public static TopicEntity createTopicEntity() {
        TopicEntity topic = new TopicEntity();
        topic.setId(100);
        topic.setTitle("Maths");
        topic.setStatus(StatusEnum.VISIBLE);
        return topic;
    }

    public static PublicExaminationEntity createPublicExaminationEntity() {
        PublicExaminationEntity exam = new PublicExaminationEntity();
        exam.setId(200);
        exam.setName("Civil Service Exam 2024");
        return exam;
    }
}