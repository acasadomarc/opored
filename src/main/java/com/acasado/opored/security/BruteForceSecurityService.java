package com.acasado.opored.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class BruteForceSecurityService {

    // After five attempts, the user is blocked to log in for 30 minutes
    private static final int MAX_ATTEMPT = 5;
    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(1);

    // Number of attempts for each user
    private final Map<String, Integer> attemptsRegistered = new ConcurrentHashMap<>();

    // Blocked users and the initial moment of their blocking
    private final Map<String, Long> lockedUsers = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsRegistered.remove(key); // Clear failed attempts on successful login
        lockedUsers.remove(key); // Unlock user on successful login
    }

    public void loginFailed(String key) {
        int attempts = attemptsRegistered.getOrDefault(key, 0);
        attempts++;
        attemptsRegistered.put(key, attempts);
        if (attempts >= MAX_ATTEMPT) {
            lockedUsers.put(key, System.currentTimeMillis()); // Lock user if max attempts exceeded
        }
    }

    public boolean isBlocked(String key) {
        if (!lockedUsers.containsKey(key)) {
            return false;
        }

        long lockTime = lockedUsers.get(key);
        if (System.currentTimeMillis() - lockTime > LOCK_TIME) {
            lockedUsers.remove(key); // Remove lock if lock time has expired
            attemptsRegistered.remove(key);
            return false;
        }

        return true; // User is still locked
    }
}
