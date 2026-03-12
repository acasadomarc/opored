package com.acasado.opored.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit Tests for BruteForceSecurityService")
class BruteForceSecurityServiceTest {

    private BruteForceSecurityService service;

    private static final int MAX_ATTEMPT = 5;
    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(1);

    @BeforeEach
    void setUp() {
        service = new BruteForceSecurityService();
    }

    @Test
    void whenIsBlocked_givenNewUser_thenReturnsFalse() {
        // Act
        boolean result = service.isBlocked("newUser");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void whenLoginFailed_givenAttemptsBelowMax_thenUserIsNotBlocked() {
        // Arrange
        String key = "user1";

        // Act: Fail 4 times (MAX is 5)
        for (int i = 0; i < MAX_ATTEMPT - 1; i++) {
            service.loginFailed(key);
        }

        // Assert
        assertThat(service.isBlocked(key)).isFalse();
    }

    @Test
    void whenLoginFailed_givenMaxAttemptsReached_thenUserIsBlocked() {
        // Arrange
        String key = "user2";

        for (int i = 0; i < MAX_ATTEMPT; i++) {
            service.loginFailed(key);
        }

        // Assert
        assertThat(service.isBlocked(key)).isTrue();
    }

    @Test
    void whenLoginSucceeded_givenPreviouslyBlockedUser_thenUserIsUnlockedAndAttemptsCleared() {
        // Arrange
        String key = "user3";
        // Block user
        for (int i = 0; i < MAX_ATTEMPT; i++) {
            service.loginFailed(key);
        }
        assertThat(service.isBlocked(key)).isTrue();

        // Act
        service.loginSucceeded(key);

        // Assert
        assertThat(service.isBlocked(key)).isFalse();

        // Verify counter restart
        service.loginFailed(key);
        assertThat(service.isBlocked(key)).isFalse();
    }

    @Test
    void whenIsBlocked_givenLockTimeExpired_thenUnlocksUser() {
        // Arrange
        String key = "user4";

        long pastTime = System.currentTimeMillis() - (LOCK_TIME + 1000);

        // Inject the user in the blocked map
        @SuppressWarnings("unchecked")
        Map<String, Long> lockedUsers = (Map<String, Long>) ReflectionTestUtils.getField(service, "lockedUsers");

        if (lockedUsers != null) {
            lockedUsers.put(key, pastTime);
            @SuppressWarnings("unchecked")
            Map<String, Integer> attemptsRegistered = (Map<String, Integer>) ReflectionTestUtils.getField(service, "attemptsRegistered");
            if (attemptsRegistered != null) {
                attemptsRegistered.put(key, MAX_ATTEMPT);
            }
        }

        // Act
        boolean isBlocked = service.isBlocked(key);

        // Assert
        assertThat(isBlocked).as("User should be auto-unlocked after time expiration").isFalse();

        // Verify that the internal maps are cleaned
        @SuppressWarnings("unchecked")
        Map<String, Long> lockedUsersAfter = (Map<String, Long>) ReflectionTestUtils.getField(service, "lockedUsers");
        assertThat(lockedUsersAfter).doesNotContainKey(key);
    }

    @Test
    void whenIsBlocked_givenLockTimeNotExpired_thenReturnsTrue() {
        // Arrange
        String key = "user5";

            // Block the user
        for (int i = 0; i < MAX_ATTEMPT; i++) {
            service.loginFailed(key);
        }

        // Act
        boolean isBlocked = service.isBlocked(key);

        // Assert
        assertThat(isBlocked).isTrue();
    }
}