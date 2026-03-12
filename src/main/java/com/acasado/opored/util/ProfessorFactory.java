package com.acasado.opored.util;

import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.model.ProfessorEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProfessorFactory {

    private static final String PROFESSOR_EMAIL = "prof@example.com";

    public static ProfessorDTO createValidProfessorDTO() {
        ProfessorDTO dto = new ProfessorDTO();
        dto.setId(1);
        dto.setName("Professor");
        dto.setSurname("User");
        dto.setAlias("alias");
        dto.setEmail(PROFESSOR_EMAIL);
        dto.setPassword("password123");
        dto.setRegistrationDate(LocalDate.now());
        dto.setEnabled(true);
        dto.setRatings(new HashSet<>());
        dto.setTotalScore(0.0f);
        return dto;
    }

    public static ProfessorEntity createValidProfessorEntity() {
        ProfessorEntity entity = new ProfessorEntity();
        entity.setId(1);
        entity.setName("Professor");
        entity.setSurname("User");
        entity.setEmail(PROFESSOR_EMAIL);
        entity.setPassword("encodedPass");
        entity.setRegistrationDate(LocalDate.now());
        entity.setEnabled(true);
        entity.setIsDeleted(false);
        entity.setRatings(new HashSet<>());
        return entity;
    }

    public static UserUpdateRequest createUserUpdateRequest() {
        return new UserUpdateRequest("ProfessorUpdated", "UserUpdated","alias", "newEncodedPass12A@d","photo");
    }

    public static AuthResponse createAuthResponse() {
        return new AuthResponse(PROFESSOR_EMAIL, "Success", "jwt-token", "refresh-token", 200);
    }
}