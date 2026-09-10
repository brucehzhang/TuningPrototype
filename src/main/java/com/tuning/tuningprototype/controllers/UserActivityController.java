package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.exceptions.UserActivityException;
import com.tuning.tuningprototype.models.requests.LogInRequest;
import com.tuning.tuningprototype.models.requests.SignUpRequest;
import com.tuning.tuningprototype.models.responses.ErrorResponse;
import com.tuning.tuningprototype.services.entity.UserActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserActivityController {

    private static final Logger log = LoggerFactory.getLogger(UserActivityController.class);

    private final UserActivityService _userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        _userActivityService = userActivityService;
    }

    /**
     * Registers a new user under an existing account and immediately logs them in.
     *
     * @param signUpRequest The details for the new user, including the account they belong to.
     * @return An AuthResponse with the new user's session token, or 400/500 with the error.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            log.info("Received signUp request for username {}", signUpRequest.username());
            return ResponseEntity.ok(_userActivityService.signUp(signUpRequest));
        } catch (Exception e) {
            String message = "Exception occurred signing up user: " + e.getMessage();
            return handleException(e, message);
        }
    }

    /**
     * Validates the provided credentials and starts a new session if they match.
     *
     * @param logInRequest The username or email and password to authenticate with.
     * @return An AuthResponse with the user's session token, or 400/500 with the error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody LogInRequest logInRequest) {
        try {
            log.info("Received logIn request for {}", logInRequest.usernameOrEmail());
            return ResponseEntity.ok(_userActivityService.logIn(logInRequest));
        } catch (Exception e) {
            String message = "Exception occurred logging in user: " + e.getMessage();
            return handleException(e, message);
        }
    }

    /**
     * Ends a session, e.g. when the user logs out.
     *
     * @param sessionToken The token of the session to end.
     * @return 200 on success, or 400/500 with the error.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logOut(@RequestParam String sessionToken) {
        try {
            log.info("Received logOut request");
            _userActivityService.logOut(sessionToken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            String message = "Exception occurred logging out user: " + e.getMessage();
            return handleException(e, message);
        }
    }

    // shared exception handling method
    private ResponseEntity<?> handleException(Exception e, String message) {
        ErrorResponse response = new ErrorResponse(message);
        if (e instanceof UserActivityException && ((UserActivityException) e).isUserError()) {
            log.warn(message);
            return ResponseEntity.badRequest().body(response);
        }
        log.error(message);
        return ResponseEntity.internalServerError().body(response);
    }
}
