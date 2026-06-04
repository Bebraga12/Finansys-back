package com.finasys.user.service;

import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.user.dto.UpdateUserRequest;
import com.finasys.user.dto.UserResponse;
import com.finasys.user.model.User;
import com.finasys.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnCurrentUser() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId, "João", "joao@example.com", UserPlan.BASIC);
        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getMe("joao@example.com");

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.name()).isEqualTo("João");
        assertThat(response.email()).isEqualTo("joao@example.com");
        assertThat(response.plan()).isEqualTo(UserPlan.BASIC);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMe("ghost@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateUserName() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId, "João", "joao@example.com", UserPlan.BASIC);
        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.updateMe("joao@example.com", new UpdateUserRequest("João Silva"));

        assertThat(response.name()).isEqualTo("João Silva");
        verify(userRepository).save(any(User.class));
    }

    private User buildUser(UUID id, String name, String email, UserPlan plan) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setPlan(plan);
        u.setPasswordHash("hash");
        return u;
    }
}
