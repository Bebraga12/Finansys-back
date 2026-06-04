package com.finasys.auth.service;

import com.finasys.auth.dto.AuthResponse;
import com.finasys.auth.dto.LoginRequest;
import com.finasys.auth.dto.RegisterRequest;
import com.finasys.auth.security.JwtTokenProvider;
import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.EmailAlreadyExistsException;
import com.finasys.user.model.User;
import com.finasys.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest("João", "joao@example.com", "senha123");
        when(userRepository.existsByEmail("joao@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenProvider.generateToken("joao@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("joao@example.com");
        assertThat(response.name()).isEqualTo("João");
        assertThat(response.plan()).isEqualTo(UserPlan.BASIC);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldAssignBasicPlanOnRegister() {
        RegisterRequest request = new RegisterRequest("Maria", "maria@example.com", "senha123");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(tokenProvider.generateToken(any())).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertThat(u.getPlan()).isEqualTo(UserPlan.BASIC);
            return u;
        });

        authService.register(request);
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("João", "existing@example.com", "senha");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("E-mail já cadastrado");
    }

    @Test
    void shouldNeverStoreRawPassword() {
        RegisterRequest request = new RegisterRequest("João", "joao@example.com", "senha123");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashed-value");
        when(tokenProvider.generateToken(any())).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertThat(u.getPasswordHash()).isNotEqualTo("senha123");
            assertThat(u.getPasswordHash()).isEqualTo("hashed-value");
            return u;
        });

        authService.register(request);
    }

    @Test
    void shouldLoginWithValidCredentials() {
        LoginRequest request = new LoginRequest("joao@example.com", "senha123");
        User user = buildUser("João", "joao@example.com", "hashed");

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "hashed")).thenReturn(true);
        when(tokenProvider.generateToken("joao@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("joao@example.com");
    }

    @Test
    void shouldThrowWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("joao@example.com", "errada");
        User user = buildUser("João", "joao@example.com", "hashed");

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void shouldThrowWhenEmailNotRegistered() {
        LoginRequest request = new LoginRequest("nobody@example.com", "senha");
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    private User buildUser(String name, String email, String hash) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(hash);
        u.setPlan(UserPlan.BASIC);
        return u;
    }
}
