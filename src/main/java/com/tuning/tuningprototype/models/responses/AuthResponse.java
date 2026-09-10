package com.tuning.tuningprototype.models.responses;

// API response for successful sign up / log in, never includes the password hash
public record AuthResponse(Long userId,
                           String username,
                           String email,
                           String sessionToken,
                           Long expiresTime) {
}
