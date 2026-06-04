package com.finasys.plan.service;

import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.InsufficientPlanException;
import com.finasys.plan.dto.PlanInfoResponse;
import com.finasys.plan.dto.UpdatePlanRequest;
import com.finasys.user.dto.UserResponse;
import com.finasys.user.model.User;
import com.finasys.user.repository.UserRepository;
import com.finasys.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @InjectMocks private PlanService planService;

    @Test
    void shouldReturnThreePlans() {
        List<PlanInfoResponse> plans = planService.listAll();
        assertThat(plans).hasSize(3);
        assertThat(plans).extracting(PlanInfoResponse::plan)
                .containsExactlyInAnyOrder(UserPlan.BASIC, UserPlan.PREMIUM, UserPlan.PREMIUM_PLUS);
    }

    @Test
    void shouldReturnCurrentPlan() {
        User user = buildUser(UserPlan.PREMIUM);
        when(userService.findUser("user@example.com")).thenReturn(user);

        PlanInfoResponse plan = planService.getCurrent("user@example.com");

        assertThat(plan.plan()).isEqualTo(UserPlan.PREMIUM);
    }

    @Test
    void shouldUpdatePlan() {
        User user = buildUser(UserPlan.BASIC);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userService.toResponse(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPlan(), u.getCreatedAt());
        });

        UserResponse response = planService.updatePlan("user@example.com", new UpdatePlanRequest(UserPlan.PREMIUM));

        assertThat(response.plan()).isEqualTo(UserPlan.PREMIUM);
    }

    @Test
    void basicUserShouldNotAccessPremiumResource() {
        User user = buildUser(UserPlan.BASIC);
        assertThatThrownBy(() -> planService.requirePlan(user, UserPlan.PREMIUM))
                .isInstanceOf(InsufficientPlanException.class);
    }

    @Test
    void premiumUserShouldNotAccessPremiumPlusResource() {
        User user = buildUser(UserPlan.PREMIUM);
        assertThatThrownBy(() -> planService.requirePlan(user, UserPlan.PREMIUM_PLUS))
                .isInstanceOf(InsufficientPlanException.class);
    }

    @Test
    void premiumPlusUserShouldAccessAllResources() {
        User user = buildUser(UserPlan.PREMIUM_PLUS);
        planService.requirePlan(user, UserPlan.BASIC);
        planService.requirePlan(user, UserPlan.PREMIUM);
        planService.requirePlan(user, UserPlan.PREMIUM_PLUS);
    }

    @Test
    void premiumUserShouldAccessPremiumResource() {
        User user = buildUser(UserPlan.PREMIUM);
        planService.requirePlan(user, UserPlan.BASIC);
        planService.requirePlan(user, UserPlan.PREMIUM);
    }

    private User buildUser(UserPlan plan) {
        User u = new User(); u.setId(UUID.randomUUID()); u.setEmail("user@example.com");
        u.setName("User"); u.setPasswordHash("hash"); u.setPlan(plan);
        return u;
    }
}
