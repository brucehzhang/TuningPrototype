package com.tuning.tuningprototype.models.requests;

// Request for logging in with either a username or an email
public record LogInRequest(String usernameOrEmail, String password) {
}
