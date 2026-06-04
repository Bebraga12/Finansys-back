package com.finasys.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret",
                "finasys-test-secret-key-must-be-at-least-32-characters-long!!");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 86400000L);
    }

    @Test
    void shouldGenerateToken() {
        String token = tokenProvider.generateToken("user@example.com");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = tokenProvider.generateToken("user@example.com");
        assertThat(tokenProvider.getEmailFromToken(token)).isEqualTo("user@example.com");
    }

    @Test
    void shouldValidateValidToken() {
        String token = tokenProvider.generateToken("user@example.com");
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThat(tokenProvider.validateToken("this.is.not.valid")).isFalse();
    }

    @Test
    void shouldRejectEmptyToken() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }
}
