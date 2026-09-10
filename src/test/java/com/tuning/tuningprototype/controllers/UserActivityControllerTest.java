package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.exceptions.UserActivityException;
import com.tuning.tuningprototype.models.enums.LicenseType;
import com.tuning.tuningprototype.models.requests.LogInRequest;
import com.tuning.tuningprototype.models.requests.SignUpRequest;
import com.tuning.tuningprototype.models.responses.AuthResponse;
import com.tuning.tuningprototype.models.responses.ErrorResponse;
import com.tuning.tuningprototype.services.entity.UserActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActivityControllerTest {

    @Mock
    private UserActivityService userActivityService;

    private UserActivityController controller;

    @BeforeEach
    void setUp() {
        controller = new UserActivityController(userActivityService);
    }

    // --- signUp ---

    @Test
    void signUp_success_returns200WithBody() {
        SignUpRequest request = new SignUpRequest("first", "middle", "last", "user1", "user1@test.com", "password", null, LicenseType.FREE);
        AuthResponse authResponse = new AuthResponse(1L, "user1", "user1@test.com", "token", 12345L);
        when(userActivityService.signUp(request)).thenReturn(authResponse);

        var response = controller.signUp(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(authResponse);
    }

    @Test
    void signUp_usernameTaken_returns400() {
        SignUpRequest request = new SignUpRequest("first", "middle", "last", "user1", "user1@test.com", "password", null, LicenseType.FREE);
        when(userActivityService.signUp(request)).thenThrow(new UserActivityException("Username is already taken.", true));

        var response = controller.signUp(request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    @Test
    void signUp_genericException_returns500() {
        SignUpRequest request = new SignUpRequest("first", "middle", "last", "user1", "user1@test.com", "password", null, LicenseType.FREE);
        when(userActivityService.signUp(request)).thenThrow(new RuntimeException("boom"));

        var response = controller.signUp(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    // --- logIn ---

    @Test
    void logIn_success_returns200WithBody() {
        LogInRequest request = new LogInRequest("user1", "password");
        AuthResponse authResponse = new AuthResponse(1L, "user1", "user1@test.com", "token", 12345L);
        when(userActivityService.logIn(request)).thenReturn(authResponse);

        var response = controller.logIn(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(authResponse);
    }

    @Test
    void logIn_invalidCredentials_returns400() {
        LogInRequest request = new LogInRequest("user1", "wrongpassword");
        when(userActivityService.logIn(request))
                .thenThrow(new UserActivityException("Invalid username/email or password.", true));

        var response = controller.logIn(request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void logIn_genericException_returns500() {
        LogInRequest request = new LogInRequest("user1", "password");
        when(userActivityService.logIn(request)).thenThrow(new RuntimeException("boom"));

        var response = controller.logIn(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- logOut ---

    @Test
    void logOut_success_returns200EmptyBody() {
        var response = controller.logOut("token123");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNull();
        verify(userActivityService).logOut("token123");
    }

    @Test
    void logOut_sessionNotFound_returns400() {
        doThrow(new UserActivityException("Session not found or already logged out.", true))
                .when(userActivityService).logOut("token123");

        var response = controller.logOut("token123");

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    @Test
    void logOut_genericException_returns500() {
        doThrow(new RuntimeException("boom")).when(userActivityService).logOut("token123");

        var response = controller.logOut("token123");

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }
}
