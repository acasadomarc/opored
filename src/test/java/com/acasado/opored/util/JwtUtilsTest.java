package com.acasado.opored.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private static final String SECRET_KEY = "super_secret_key_for_testing";
    private static final String USER_GENERATOR = "opored-app-test";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "privateKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtils, "userGenerator", USER_GENERATOR);
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenCreateToken_thenReturnValidJwtString() {
        // Arrange
        String username = "testUser";
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(authentication.getPrincipal()).thenReturn(username);

        when(authentication.getAuthorities()).thenReturn((List) authorities); // Cast needed for generic raw type mocking safety

        // Act
        String token = jwtUtils.createToken(authentication);

        // Assert
        assertThat(token).isNotNull().isNotEmpty();

        // Manual verification to ensure the createToken logic worked as expected
        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);

        assertThat(decoded.getSubject()).isEqualTo(username);
        assertThat(decoded.getIssuer()).isEqualTo(USER_GENERATOR);
        assertThat(decoded.getClaim("authorities").asString()).contains("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should validate and decode a correct token successfully")
    void whenValidateToken_givenValidToken_thenReturnDecodedJWT() {
        // Arrange
        String token = generateTestToken(SECRET_KEY, USER_GENERATOR, "user1", 10000);

        // Act
        DecodedJWT result = jwtUtils.validateToken(token);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSubject()).isEqualTo("user1");
        assertThat(result.getIssuer()).isEqualTo(USER_GENERATOR);
    }

    @Test
    @DisplayName("Should throw exception when validating token signed with wrong key")
    void whenValidateToken_givenWrongSignature_thenThrowException() {
        // Arrange
        String wrongSecret = "wrong_secret_key";
        String token = generateTestToken(wrongSecret, USER_GENERATOR, "user1", 10000);

        // Act & Assert
        assertThatThrownBy(() -> jwtUtils.validateToken(token))
                .isInstanceOf(JWTVerificationException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    @DisplayName("Should throw exception when validating token with wrong issuer")
    void whenValidateToken_givenWrongIssuer_thenThrowException() {
        // Arrange
        String wrongIssuer = "fake-app";
        String token = generateTestToken(SECRET_KEY, wrongIssuer, "user1", 10000);

        // Act & Assert
        assertThatThrownBy(() -> jwtUtils.validateToken(token))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Should throw exception when validating expired token")
    void whenValidateToken_givenExpiredToken_thenThrowException() {
        // Arrange: Token expired 1 second ago (-1000ms)
        String token = generateTestToken(SECRET_KEY, USER_GENERATOR, "user1", -1000);

        // Act & Assert
        assertThatThrownBy(() -> jwtUtils.validateToken(token))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Should extract username correctly from DecodedJWT")
    void whenExtractUser_thenReturnSubject() {
        // Arrange
        String token = generateTestToken(SECRET_KEY, USER_GENERATOR, "targetUser", 10000);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        // Act
        String user = jwtUtils.extractUser(decodedJWT);

        // Assert
        assertThat(user).isEqualTo("targetUser");
    }

    @Test
    @DisplayName("Should retrieve specific claim correctly")
    void whenGetSpecificClaim_thenReturnClaimValue() {
        // Arrange
        // Manually create token with specific claim using Library directly for setup
        String token = JWT.create()
                .withIssuer(USER_GENERATOR)
                .withClaim("customData", "HelloWorld")
                .sign(Algorithm.HMAC256(SECRET_KEY));

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        // Act
        var claim = jwtUtils.getSpecificClaim(decodedJWT, "customData");

        // Assert
        assertThat(claim).isNotNull();
        assertThat(claim.asString()).isEqualTo("HelloWorld");
    }

    // --- Helper Method to generate tokens independently of the tested class ---
    private String generateTestToken(String secret, String issuer, String subject, long expiryOffsetMillis) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryOffsetMillis))
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(secret));
    }
}