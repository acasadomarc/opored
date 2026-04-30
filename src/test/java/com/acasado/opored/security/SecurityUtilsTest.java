package com.acasado.opored.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void When_GetCurrentUserId_WithAuth_Expect_Id() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("123", null)
        );

        // Act
        Integer userId = SecurityUtils.getCurrentUserId();

        // Assert
        assertEquals(123, userId);
    }

    @Test
    void When_GetCurrentUserId_WithoutAuth_Expect_Null() {
        // Act
        Integer userId = SecurityUtils.getCurrentUserId();

        // Assert
        assertNull(userId);
    }

    @Test
    void When_IsUserRoot_WithRootAuthority_Expect_True() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, List.of(new SimpleGrantedAuthority("ROOT")))
        );

        // Act & Assert
        assertTrue(SecurityUtils.isUserRoot());
    }

    @Test
    void When_IsUserAdmin_WithAdminAuthority_Expect_True() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );

        // Act & Assert
        assertTrue(SecurityUtils.isUserAdmin());
    }

    @Test
    void When_IsProvidedUser_WithSameId_Expect_True() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("123", null)
        );

        // Act & Assert
        assertTrue(SecurityUtils.isProvidedUser(123));
        assertFalse(SecurityUtils.isProvidedUser(456));
    }

    @Test
    void Test_EmailValidation() {
        assertTrue(SecurityUtils.emailValidation("test@example.com"));
        assertFalse(SecurityUtils.emailValidation("invalid-email"));
        assertFalse(SecurityUtils.emailValidation(null));
    }

    @Test
    void Test_PasswordValidation() {
        assertTrue(SecurityUtils.passwordValidation("SecureP@ss123!"));
        assertFalse(SecurityUtils.passwordValidation("weak"));
        assertFalse(SecurityUtils.passwordValidation("NoSpecialChar123"));
    }

    @Test
    void Test_AliasValidation() {
        assertTrue(SecurityUtils.aliasValidation("validAlias123"));
        assertFalse(SecurityUtils.aliasValidation("sh"));
        assertFalse(SecurityUtils.aliasValidation("TooLongAliasExceedingTwentyCharacters"));
        assertFalse(SecurityUtils.aliasValidation("invalid spaces"));
    }

    @Test
    void When_IsUserRoot_WithNoRootAuthority_Expect_False() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );

        // Act & Assert
        assertFalse(SecurityUtils.isUserRoot());
    }

    @Test
    void When_IsUserAdmin_WithNoAdminAuthority_Expect_False() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
        );

        // Act & Assert
        assertFalse(SecurityUtils.isUserAdmin());
    }

    @Test
    void Test_EmailValidation_LengthLimit() {
        String longEmail = "a".repeat(243) + "@example.com"; // 255 chars
        assertFalse(SecurityUtils.emailValidation(longEmail));
    }

    @Test
    void Test_PasswordValidation_FailureModes() {
        assertFalse(SecurityUtils.passwordValidation("NoDigitLowerUpper!"), "Missing digit");
        assertFalse(SecurityUtils.passwordValidation("NODIGIT123UPPER!"), "Missing lowercase");
        assertFalse(SecurityUtils.passwordValidation("nodigit123lower!"), "Missing uppercase");
        assertFalse(SecurityUtils.passwordValidation("Short1@a"), "Too short");
        assertFalse(SecurityUtils.passwordValidation("VeryLongPasswordExceedingLimit123@abc"), "Too long");
    }

    @Test
    void Test_PublicUserAliasValidation_AdditionalBannedWords() {
        assertFalse(SecurityUtils.publicUserAliasValidation("soporteUser"));
        assertFalse(SecurityUtils.publicUserAliasValidation("eliminaMe"));
        assertFalse(SecurityUtils.publicUserAliasValidation("borraTodo"));
        assertFalse(SecurityUtils.publicUserAliasValidation("userDelete"));
    }

    @Test
    void Test_PrivilegedUserAliasValidation() {
        assertTrue(SecurityUtils.privilegedUserAliasValidation("admin"));
        assertFalse(SecurityUtils.privilegedUserAliasValidation("sh"));
    }
}
