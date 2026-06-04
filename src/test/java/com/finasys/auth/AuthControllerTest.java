package com.finasys.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.finasys.auth.controller.AuthController;
import com.finasys.auth.dto.AuthResponse;
import com.finasys.auth.dto.LoginRequest;
import com.finasys.auth.dto.RegisterRequest;
import com.finasys.auth.security.JwtTokenProvider;
import com.finasys.auth.service.AuthService;
import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.EmailAlreadyExistsException;
import com.finasys.common.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private com.finasys.user.service.UserService userService;

    @Test
    void shouldRegisterAndReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("João", "joao@example.com", "senha123");
        AuthResponse response = new AuthResponse("token", "joao@example.com", "João", UserPlan.BASIC);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.email").value("joao@example.com"))
                .andExpect(jsonPath("$.name").value("João"));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("João", "existing@example.com", "senha123");
        when(authService.register(any())).thenThrow(new EmailAlreadyExistsException("E-mail já cadastrado."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn400WhenRegisterDataIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest("", "", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginAndReturn200() throws Exception {
        LoginRequest request = new LoginRequest("joao@example.com", "senha123");
        AuthResponse response = new AuthResponse("token", "joao@example.com", "João", UserPlan.BASIC);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    void shouldReturn401WhenCredentialsInvalid() throws Exception {
        LoginRequest request = new LoginRequest("joao@example.com", "errada");
        when(authService.login(any())).thenThrow(new BadCredentialsException("Credenciais inválidas."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
