package com.finasys.goal.service;

import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.goal.dto.GoalDepositRequest;
import com.finasys.goal.dto.GoalRequest;
import com.finasys.goal.dto.GoalResponse;
import com.finasys.goal.model.Goal;
import com.finasys.goal.repository.GoalRepository;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @Mock private UserService userService;
    @InjectMocks private GoalService goalService;

    @Test
    void shouldCreateGoal() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(goalRepository.save(any())).thenAnswer(inv -> { Goal g = inv.getArgument(0); g.setId(UUID.randomUUID()); return g; });

        GoalResponse response = goalService.create("user@example.com",
                new GoalRequest("Viagem", new BigDecimal("5000"), BigDecimal.ZERO, LocalDate.now().plusMonths(6)));

        assertThat(response.title()).isEqualTo("Viagem");
        assertThat(response.targetAmount()).isEqualByComparingTo("5000");
    }

    @Test
    void shouldCalculateProgressPercent() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        Goal goal = buildGoal(UUID.randomUUID(), new BigDecimal("1000"), new BigDecimal("250"), user);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal));

        List<GoalResponse> result = goalService.listAll("user@example.com");

        assertThat(result.get(0).progressPercent()).isEqualTo(25.0);
    }

    @Test
    void shouldListOnlyUserGoals() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(buildGoal(UUID.randomUUID(), new BigDecimal("1000"), BigDecimal.ZERO, user)));

        assertThat(goalService.listAll("user@example.com")).hasSize(1);
    }

    @Test
    void shouldThrow404WhenGoalNotFound() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getById("user@example.com", goalId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDepositAmountToGoal() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        User user = buildUser(userId);
        Goal goal = buildGoal(goalId, new BigDecimal("1000"), new BigDecimal("200"), user);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GoalResponse response = goalService.deposit("user@example.com", goalId,
                new GoalDepositRequest(new BigDecimal("300")));

        assertThat(response.currentAmount()).isEqualByComparingTo("500");
    }

    private User buildUser(UUID id) {
        User u = new User(); u.setId(id); u.setEmail("user@example.com");
        u.setName("User"); u.setPasswordHash("hash"); u.setPlan(UserPlan.BASIC);
        return u;
    }

    private Goal buildGoal(UUID id, BigDecimal target, BigDecimal current, User user) {
        Goal g = new Goal(); g.setId(id); g.setTitle("Meta"); g.setUser(user);
        g.setTargetAmount(target); g.setCurrentAmount(current);
        g.setDeadline(LocalDate.now().plusMonths(12));
        return g;
    }
}
