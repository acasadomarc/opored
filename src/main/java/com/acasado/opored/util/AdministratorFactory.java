package com.acasado.opored.util;

import com.acasado.opored.dto.AdministratorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.model.AdministratorEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AdministratorFactory {

    private static final String ADMIN_EMAIL = "john.doe@example.com";

    public static AdministratorEntity createValidAdministratorEntity() {
        AdministratorEntity entity = new AdministratorEntity();
        entity.setId(1);
        entity.setName("John");
        entity.setSurname("Doe");
        entity.setEmail(ADMIN_EMAIL);
        entity.setPassword("encodedPassword");
        entity.setRegistrationDate(LocalDate.now());
        entity.setEnabled(true);
        entity.setAccountNoExpired(true);
        entity.setAccountNoLocked(true);
        entity.setCredentialNoExpired(true);
        entity.setIsDeleted(false);
        return entity;
    }

    public static AdministratorDTO createValidAdministratorDTO() {
        return AdministratorDTO.builder()
                .id(1)
                .name("John")
                .surname("Doe")
                .alias("alias")
                .email(ADMIN_EMAIL)
                .password("password123")
                .registrationDate(LocalDate.now())
                .profilePhoto("/route/to/photo")
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }

    public static AdministratorDTO createInvalidAdministratorDTO() {
        return AdministratorDTO.builder()
                .id(null)
                .name(null)
                .surname(null)
                .alias(null)
                .email("invalid-email")
                .password("")
                .registrationDate(null)
                .profilePhoto(null)
                .isEnabled(false)
                .accountNoExpired(false)
                .accountNoLocked(false)
                .credentialNoExpired(false)
                .build();
    }

    public static AuthResponse createAuthResponse() {
        return new AuthResponse(
                ADMIN_EMAIL,
                "User created successfully",
                "mock-jwt-token",
                "refresh-token",
                200
        );
    }

    public static UserUpdateRequest createUserUpdateRequest() {
        return new UserUpdateRequest("AdministratorUpdated", "UserUpdated","alias", "newEncodedPass12A@d","photo");
    }
}