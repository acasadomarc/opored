package com.acasado.opored.util;

import com.acasado.opored.dto.ModeratorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.model.ModeratorEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ModeratorFactory {

    private static final String MODERATOR_EMAIL = "mod@example.com";

    public static ModeratorDTO createValidModeratorDTO() {
        return ModeratorDTO.builder()
                .id(1)
                .name("Moderator")
                .surname("User")
                .alias("alias")
                .email(MODERATOR_EMAIL)
                .password("password123@4jtA")
                .registrationDate(LocalDate.now())
                .profilePhoto("/route/to/photo")
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }

    public static ModeratorDTO createInvalidModeratorDTO() {
        return ModeratorDTO.builder()
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

    public static ModeratorEntity createValidModeratorEntity() {
        ModeratorEntity entity = new ModeratorEntity();
        entity.setId(1);
        entity.setName("Moderator");
        entity.setSurname("User");
        entity.setEmail(MODERATOR_EMAIL);
        entity.setPassword("encodedPass");
        entity.setRegistrationDate(LocalDate.now());
        entity.setEnabled(true);
        entity.setIsDeleted(false);
        entity.setAccountNoExpired(true);
        entity.setAccountNoLocked(true);
        entity.setCredentialNoExpired(true);
        return entity;
    }

    public static UserUpdateRequest createUserUpdateRequest() {
        return new UserUpdateRequest("ModeratorUpdated", "UserUpdated","alias", "newEncodedPass12A@d","photo");
    }

    public static AuthResponse createAuthResponse() {
        return new AuthResponse(MODERATOR_EMAIL, "Success", "jwt-token","refresh-token", 200);
    }
}