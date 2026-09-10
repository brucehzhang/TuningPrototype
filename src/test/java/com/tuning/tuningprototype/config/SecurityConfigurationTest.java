package com.tuning.tuningprototype.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigurationTest {

    @Test
    void passwordEncoder_returnsBCryptPasswordEncoder_thatEncodesAndMatchesRoundTrip() {
        SecurityConfiguration configuration = new SecurityConfiguration();

        PasswordEncoder passwordEncoder = configuration.passwordEncoder();

        assertThat(passwordEncoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);

        String rawPassword = "super-secret-password";
        String encoded = passwordEncoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encoded)).isTrue();
        assertThat(passwordEncoder.matches("wrong-password", encoded)).isFalse();
    }
}
