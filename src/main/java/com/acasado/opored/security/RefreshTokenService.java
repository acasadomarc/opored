package com.acasado.opored.security;

import com.acasado.opored.model.RefreshTokenEntity;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.RefreshTokenRepository;
import com.acasado.opored.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private static final long refreshDurationMs = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public RefreshTokenEntity createRefreshToken(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        RefreshTokenEntity token = new RefreshTokenEntity();

        token.setUser(userEntity);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshDurationMs));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    public RefreshTokenEntity verifyExpiration(String token) {
        RefreshTokenEntity refreshToken = getRefreshToken(token);

        if (refreshToken.getExpiryDate().isBefore(Instant.now()) || refreshToken.isRevoked()) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;
    }

    public RefreshTokenEntity getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new EntityNotFoundException("Refresh token not found"));
    }

    public void revoke(RefreshTokenEntity refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}