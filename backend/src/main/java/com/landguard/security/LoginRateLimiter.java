package com.landguard.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_SECONDS = 900; // 15 minutes

    private static class Attempt {
        int count;
        Instant lockoutUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) return false;

        if (attempt.lockoutUntil != null) {
            if (Instant.now().isBefore(attempt.lockoutUntil)) {
                return true;
            } else {
                // Lockout expired
                attempts.remove(key);
                return false;
            }
        }
        return false;
    }

    public void recordFailedAttempt(String key) {
        attempts.compute(key, (k, attempt) -> {
            if (attempt == null) {
                attempt = new Attempt();
            }
            attempt.count++;
            if (attempt.count >= MAX_ATTEMPTS) {
                attempt.lockoutUntil = Instant.now().plusSeconds(LOCKOUT_DURATION_SECONDS);
            }
            return attempt;
        });
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }
}
