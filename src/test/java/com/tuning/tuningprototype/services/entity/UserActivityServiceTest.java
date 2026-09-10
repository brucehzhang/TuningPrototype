package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.UserActivityException;
import com.tuning.tuningprototype.models.db.entity.Account;
import com.tuning.tuningprototype.models.db.entity.Session;
import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.enums.LicenseType;
import com.tuning.tuningprototype.models.mappers.request.UserActivityRequestMapper;
import com.tuning.tuningprototype.models.requests.LogInRequest;
import com.tuning.tuningprototype.models.requests.SignUpRequest;
import com.tuning.tuningprototype.models.responses.AuthResponse;
import com.tuning.tuningprototype.repositories.AccountRepository;
import com.tuning.tuningprototype.repositories.SessionRepository;
import com.tuning.tuningprototype.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserActivityRequestMapper userActivityRequestMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserActivityService service;

    @BeforeEach
    void setUp() {
        service = new UserActivityService(userRepository, sessionRepository, accountRepository,
                userActivityRequestMapper, passwordEncoder);
    }

    private SignUpRequest signUpRequest(Long accountId) {
        return new SignUpRequest("First", null, "Last", "user1", "user1@example.com",
                "rawPassword", accountId, LicenseType.FREE);
    }

    // --- signUp ---

    @Test
    void signUp_withAccountId_success() {
        SignUpRequest request = signUpRequest(2L);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(Account.builder().id(2L).build()));
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");
        User builtUser = User.builder().username("user1").email("user1@example.com").build();
        when(userActivityRequestMapper.toUserEntity(eq(request), eq("hashed"), anyLong())).thenReturn(builtUser);
        User savedUser = User.builder().id(5L).username("user1").email("user1@example.com").build();
        when(userRepository.save(builtUser)).thenReturn(savedUser);
        Session builtSession = Session.builder().sessionToken("token").expiresTime(123L).build();
        when(userActivityRequestMapper.toSessionEntity(eq(5L), anyString(), anyLong(), anyLong())).thenReturn(builtSession);
        when(sessionRepository.save(builtSession)).thenReturn(builtSession);

        AuthResponse response = service.signUp(request);

        assertThat(response.userId()).isEqualTo(5L);
        assertThat(response.username()).isEqualTo("user1");
        assertThat(response.email()).isEqualTo("user1@example.com");
        assertThat(response.sessionToken()).isEqualTo("token");
        assertThat(response.expiresTime()).isEqualTo(123L);
    }

    @Test
    void signUp_nullAccountId_skipsAccountLookup() {
        SignUpRequest request = signUpRequest(null);
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");
        User builtUser = User.builder().username("user1").email("user1@example.com").build();
        when(userActivityRequestMapper.toUserEntity(eq(request), eq("hashed"), anyLong())).thenReturn(builtUser);
        User savedUser = User.builder().id(5L).username("user1").email("user1@example.com").build();
        when(userRepository.save(builtUser)).thenReturn(savedUser);
        Session builtSession = Session.builder().sessionToken("token").expiresTime(123L).build();
        when(userActivityRequestMapper.toSessionEntity(eq(5L), anyString(), anyLong(), anyLong())).thenReturn(builtSession);
        when(sessionRepository.save(builtSession)).thenReturn(builtSession);

        service.signUp(request);

        verifyNoInteractions(accountRepository);
    }

    @Test
    void signUp_accountIdProvided_accountNotFound_throwsUserError() {
        SignUpRequest request = signUpRequest(2L);
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.signUp(request))
                .isInstanceOf(UserActivityException.class)
                .satisfies(e -> assertThat(((UserActivityException) e).isUserError()).isTrue());

        verifyNoInteractions(userRepository, passwordEncoder, sessionRepository);
    }

    @Test
    void signUp_usernameTaken_throwsUserError() {
        SignUpRequest request = signUpRequest(null);
        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertThatThrownBy(() -> service.signUp(request))
                .isInstanceOf(UserActivityException.class)
                .satisfies(e -> assertThat(((UserActivityException) e).isUserError()).isTrue());

        verify(userRepository, never()).existsByEmail(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void signUp_emailTaken_throwsUserError() {
        SignUpRequest request = signUpRequest(null);
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.signUp(request))
                .isInstanceOf(UserActivityException.class)
                .satisfies(e -> assertThat(((UserActivityException) e).isUserError()).isTrue());

        verifyNoInteractions(passwordEncoder);
    }

    // --- logIn ---

    @Test
    void logIn_success() {
        LogInRequest request = new LogInRequest("user1", "rawPassword");
        User user = User.builder().id(5L).username("user1").email("user1@example.com").passwordHash("hashed").build();
        when(userRepository.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "hashed")).thenReturn(true);
        Session builtSession = Session.builder().sessionToken("token").expiresTime(123L).build();
        when(userActivityRequestMapper.toSessionEntity(eq(5L), anyString(), anyLong(), anyLong())).thenReturn(builtSession);
        when(sessionRepository.save(builtSession)).thenReturn(builtSession);

        AuthResponse response = service.logIn(request);

        assertThat(response.userId()).isEqualTo(5L);
        assertThat(response.sessionToken()).isEqualTo("token");
    }

    @Test
    void logIn_userNotFound_throwsGenericMessage() {
        LogInRequest request = new LogInRequest("nouser", "rawPassword");
        when(userRepository.findByUsernameOrEmail("nouser", "nouser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.logIn(request))
                .isInstanceOf(UserActivityException.class)
                .hasMessage("Invalid username/email or password.")
                .satisfies(e -> assertThat(((UserActivityException) e).isUserError()).isTrue());

        verifyNoInteractions(passwordEncoder, sessionRepository);
    }

    @Test
    void logIn_wrongPassword_throwsSameGenericMessageAsUserNotFound() {
        LogInRequest request = new LogInRequest("user1", "wrongPassword");
        User user = User.builder().id(5L).username("user1").passwordHash("hashed").build();
        when(userRepository.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> service.logIn(request))
                .isInstanceOf(UserActivityException.class)
                .hasMessage("Invalid username/email or password.")
                .satisfies(e -> assertThat(((UserActivityException) e).isUserError()).isTrue());

        verifyNoInteractions(sessionRepository);
    }

    // --- logOut ---

    @Test
    void logOut_success_deletesSession() {
        Session session = Session.builder().id(1L).sessionToken("token").build();
        when(sessionRepository.findBySessionToken("token")).thenReturn(Optional.of(session));

        service.logOut("token");

        verify(sessionRepository).delete(session);
    }

    @Test
    void logOut_sessionNotFound_throwsUserError() {
        when(sessionRepository.findBySessionToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.logOut("missing"))
                .isInstanceOf(UserActivityException.class)
                .satisfies(e -> assertThat(((UserActivityException) e).isUserError()).isTrue());

        verify(sessionRepository, never()).delete(any());
    }

    // --- session expiry ---

    @Test
    void signUp_setsSessionExpiryToSevenDaysFromCreation() {
        SignUpRequest request = signUpRequest(null);
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");
        User builtUser = User.builder().username("user1").build();
        when(userActivityRequestMapper.toUserEntity(eq(request), eq("hashed"), anyLong())).thenReturn(builtUser);
        User savedUser = User.builder().id(5L).username("user1").build();
        when(userRepository.save(builtUser)).thenReturn(savedUser);
        when(userActivityRequestMapper.toSessionEntity(eq(5L), anyString(), anyLong(), anyLong()))
                .thenAnswer(invocation -> Session.builder()
                        .userId(invocation.getArgument(0))
                        .sessionToken(invocation.getArgument(1))
                        .createdTime(invocation.getArgument(2))
                        .expiresTime(invocation.getArgument(3))
                        .build());
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.signUp(request);

        ArgumentCaptor<Long> nowCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> expiresCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userActivityRequestMapper).toSessionEntity(eq(5L), anyString(), nowCaptor.capture(), expiresCaptor.capture());
        long sessionDurationSeconds = 7 * 24 * 60 * 60;
        assertThat(expiresCaptor.getValue() - nowCaptor.getValue()).isEqualTo(sessionDurationSeconds);
    }
}
