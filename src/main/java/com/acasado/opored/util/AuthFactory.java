package com.acasado.opored.util;

import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthFactory {

    private static final String AUTH_EMAIL = "user@example.com";

    public static AuthLoginRequest createValidLoginRequest() {
        return new AuthLoginRequest(AUTH_EMAIL, "Password123!@#");
    }

    public static AuthLoginRequest createInvalidLoginRequest() {
        return new AuthLoginRequest(null, null);
    }

    public static AuthCreateUserRequest createValidRegisterRequest(RoleEnum role) {
        return new AuthCreateUserRequest(
                "Name",
                "Surname",
                "Alias",
                "valid.email@example.com",
                "Password123!@#",
                role.toString()
        );
    }

    public static AuthCreateUserRequest createInvalidPasswordRegisterRequest(RoleEnum role) {
        return new AuthCreateUserRequest(
                "Name",
                "Surname",
                "Alias",
                "valid.email@example.com",
                "weak",
                role.toString()
        );
    }

    public static AuthResponse createAuthResponse() {
        return new AuthResponse(AUTH_EMAIL, "Success", "jwt-token","refresh-token", 200);
    }

    public static RoleEntity createRoleEntity(RoleEnum roleEnum) {
        RoleEntity role = new RoleEntity();
        role.setName(roleEnum);
        role.setPermissions(new HashSet<>());
        return role;
    }

    public static UserEntity createUserEntity(RoleEnum roleEnum) {
        // Using Administrator as a concrete implementation of UserEntity for tests
        AdministratorEntity user = new AdministratorEntity();
        user.setId(1);
        user.setEmail(AUTH_EMAIL);
        user.setPassword("encodedPassword");
        user.setRole(createRoleEntity(roleEnum));
        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setCredentialNoExpired(true);
        user.setAccountNoLocked(true);
        return user;
    }

    public static RefreshTokenEntity createRefreshTokenEntity() {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken("refresh-token");
        return refreshToken;
    }
}