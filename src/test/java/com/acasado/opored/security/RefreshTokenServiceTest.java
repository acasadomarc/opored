package com.acasado.opored.security;

import com.acasado.opored.exception.RefreshTokenExpiredException;
import com.acasado.opored.model.RefreshTokenEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.RefreshTokenRepository;
import com.acasado.opored.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void When_CreateRefreshToken_Expect_SavedAndReturned() {
        // Arrange
        int userId = 1;
        UserEntity user = new StudentEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RefreshTokenEntity result = refreshTokenService.createRefreshToken(userId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(user, result.getUser());
        assertFalse(result.isRevoked());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));

        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void Expect_BadCredentialsException_When_CreateRefreshToken_UserNotFound() {
        // Arrange
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> refreshTokenService.createRefreshToken(99));
        verify(refreshTokenRepository, never()).save(any());
    }

    // --- Verify Expiration Tests ---

    @Test
    void When_VerifyExpiration_WithValidToken_Expect_Returned() {
        // Arrange
        RefreshTokenEntity validToken = createMockToken(Instant.now().plusSeconds(3600), false);
        when(refreshTokenRepository.findByToken(validToken.getToken())).thenReturn(Optional.of(validToken));

        // Act
        RefreshTokenEntity result = refreshTokenService.verifyExpiration(validToken.getToken());

        // Assert
        assertEquals(validToken, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void Expect_RefreshTokenExpiredException_When_VerifyExpiration_TimeExpired() {
        // Arrange
        RefreshTokenEntity expiredToken = createMockToken(Instant.now().minusSeconds(3600), false);
        when(refreshTokenRepository.findByToken(expiredToken.getToken())).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        String token = expiredToken.getToken();
        assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.verifyExpiration(token));

        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void Expect_RefreshTokenExpiredException_When_VerifyExpiration_TokenRevoked() {
        // Arrange
        RefreshTokenEntity revokedToken = createMockToken(Instant.now().plusSeconds(3600), true); // Fecha válida, pero revocado
        when(refreshTokenRepository.findByToken(revokedToken.getToken())).thenReturn(Optional.of(revokedToken));

        // Act & Assert
        String token = revokedToken.getToken();
        assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.verifyExpiration(token));

        verify(refreshTokenRepository).delete(revokedToken);
    }

    // --- Retrieve & Revoke Tests ---

    @Test
    void When_GetRefreshToken_Expect_Entity() {
        // Arrange
        String tokenStr = "mock-token-uuid";
        RefreshTokenEntity token = new RefreshTokenEntity();
        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        // Act
        RefreshTokenEntity result = refreshTokenService.getRefreshToken(tokenStr);

        // Assert
        assertNotNull(result);
    }

    @Test
    void Expect_EntityNotFoundException_When_GetRefreshToken_NotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> refreshTokenService.getRefreshToken("invalid-token"));
    }

    @Test
    void When_Revoke_Expect_RevokedSetToTrueAndSaved() {
        // Arrange
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setRevoked(false);

        // Act
        refreshTokenService.revoke(token);

        // Assert
        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(captor.capture());

        assertTrue(captor.getValue().isRevoked());
    }

    // --- Helper Methods ---

    private RefreshTokenEntity createMockToken(Instant expiryDate, boolean isRevoked) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setToken("mock-uuid-token");
        token.setExpiryDate(expiryDate);
        token.setRevoked(isRevoked);
        return token;
    }
}