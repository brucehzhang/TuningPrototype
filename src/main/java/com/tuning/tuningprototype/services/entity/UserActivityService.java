package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.UserActivityException;
import com.tuning.tuningprototype.models.db.entity.Session;
import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.mappers.request.UserActivityRequestMapper;
import com.tuning.tuningprototype.models.requests.LogInRequest;
import com.tuning.tuningprototype.models.requests.SignUpRequest;
import com.tuning.tuningprototype.models.responses.AuthResponse;
import com.tuning.tuningprototype.repositories.AccountRepository;
import com.tuning.tuningprototype.repositories.SessionRepository;
import com.tuning.tuningprototype.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class UserActivityService {

    // Sessions are valid for 7 days from log in.
    private static final long SESSION_DURATION_SECONDS = 7 * 24 * 60 * 60;

    private final UserRepository _userRepository;
    private final SessionRepository _sessionRepository;
    private final AccountRepository _accountRepository;
    private final UserActivityRequestMapper _userActivityRequestMapper;
    private final PasswordEncoder _passwordEncoder;
    private final SecureRandom _secureRandom = new SecureRandom();

    public UserActivityService(UserRepository userRepository, SessionRepository sessionRepository, AccountRepository accountRepository, UserActivityRequestMapper userActivityRequestMapper, PasswordEncoder passwordEncoder) {
        _userRepository = userRepository;
        _sessionRepository = sessionRepository;
        _accountRepository = accountRepository;
        _userActivityRequestMapper = userActivityRequestMapper;
        _passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user under an existing account and immediately logs them in.
     *
     * @param signUpRequest The details for the new user, including the account they belong to.
     * @return An AuthResponse with the new user's session token.
     */
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        if (signUpRequest.accountId() != null && _accountRepository.findById(signUpRequest.accountId()).isEmpty()) {
            throw new UserActivityException("No account found for the provided accountId " + signUpRequest.accountId(), true);
        }
        if (_userRepository.existsByUsername(signUpRequest.username())) {
            throw new UserActivityException("Username is already taken.", true);
        }
        if (_userRepository.existsByEmail(signUpRequest.email())) {
            throw new UserActivityException("Email is already registered.", true);
        }

        long now = Instant.now().getEpochSecond();
        String passwordHash = _passwordEncoder.encode(signUpRequest.password());
        User user = _userRepository.save(_userActivityRequestMapper.toUserEntity(signUpRequest, passwordHash, now));
        return createSession(user, now);
    }

    /**
     * Validates the provided credentials and starts a new session if they match.
     *
     * @param logInRequest The username or email and password to authenticate with.
     * @return An AuthResponse with the user's session token.
     */
    public AuthResponse logIn(LogInRequest logInRequest) {
        User user = _userRepository.findByUsernameOrEmail(logInRequest.usernameOrEmail(), logInRequest.usernameOrEmail())
                .orElseThrow(() -> new UserActivityException("Invalid username/email or password.", true));
        if (!_passwordEncoder.matches(logInRequest.password(), user.getPasswordHash())) {
            // Same message as the not-found case above, to avoid revealing whether the account exists.
            throw new UserActivityException("Invalid username/email or password.", true);
        }

        return createSession(user, Instant.now().getEpochSecond());
    }

    /**
     * Ends a session, e.g. when the user logs out.
     *
     * @param sessionToken The token of the session to end.
     */
    public void logOut(String sessionToken) {
        Session session = _sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new UserActivityException("Session not found or already logged out.", true));
        _sessionRepository.delete(session);
    }

    private AuthResponse createSession(User user, long nowEpochSeconds) {
        String sessionToken = generateSessionToken();
        long expiresTime = nowEpochSeconds + SESSION_DURATION_SECONDS;
        Session session = _sessionRepository.save(
                _userActivityRequestMapper.toSessionEntity(user.getId(), sessionToken, nowEpochSeconds, expiresTime));
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), session.getSessionToken(), session.getExpiresTime());
    }

    // 256 bits of randomness, URL-safe so it can be passed around freely without encoding.
    private String generateSessionToken() {
        byte[] bytes = new byte[32];
        _secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
