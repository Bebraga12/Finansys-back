package com.finasys.dashboard.service;

import com.finasys.common.enums.TransactionType;
import com.finasys.common.enums.UserPlan;
import com.finasys.dashboard.dto.DashboardSummaryResponse;
import com.finasys.transaction.model.Transaction;
import com.finasys.transaction.repository.TransactionRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private UserService userService;
    @InjectMocks private DashboardService dashboardService;

    @Test
    void shouldCalculateCurrentBalance() {
        User user = buildUser(UserPlan.BASIC);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserId(any(UUID.class))).thenReturn(List.of(
                buildTransaction(TransactionType.INCOME, new BigDecimal("1000"), LocalDate.now()),
                buildTransaction(TransactionType.EXPENSE, new BigDecimal("300"), LocalDate.now()),
                buildTransaction(TransactionType.INCOME, new BigDecimal("500"), LocalDate.now())
        ));

        DashboardSummaryResponse summary = dashboardService.getSummary("user@example.com");

        assertThat(summary.currentBalance()).isEqualByComparingTo("1200.00");
    }

    @Test
    void shouldCalculateMonthlyIncome() {
        User user = buildUser(UserPlan.BASIC);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserId(any(UUID.class))).thenReturn(List.of(
                buildTransaction(TransactionType.INCOME, new BigDecimal("2000"), LocalDate.now()),
                buildTransaction(TransactionType.EXPENSE, new BigDecimal("500"), LocalDate.now())
        ));

        DashboardSummaryResponse summary = dashboardService.getSummary("user@example.com");

        assertThat(summary.monthlyIncome()).isEqualByComparingTo("2000.00");
        assertThat(summary.monthlyExpense()).isEqualByComparingTo("500.00");
    }

    @Test
    void basicUserShouldHaveLockedFeatures() {
        User user = buildUser(UserPlan.BASIC);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserId(any(UUID.class))).thenReturn(List.of());

        DashboardSummaryResponse summary = dashboardService.getSummary("user@example.com");

        assertThat(summary.hasLockedFeatures()).isTrue();
        assertThat(summary.currentPlan()).isEqualTo(UserPlan.BASIC);
    }

    @Test
    void premiumPlusUserShouldNotHaveLockedFeatures() {
        User user = buildUser(UserPlan.PREMIUM_PLUS);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserId(any(UUID.class))).thenReturn(List.of());

        assertThat(dashboardService.getSummary("user@example.com").hasLockedFeatures()).isFalse();
    }

    private User buildUser(UserPlan plan) {
        User u = new User(); u.setId(UUID.randomUUID()); u.setEmail("user@example.com");
        u.setName("User"); u.setPasswordHash("hash"); u.setPlan(plan);
        return u;
    }

    private Transaction buildTransaction(TransactionType type, BigDecimal amount, LocalDate date) {
        Transaction t = new Transaction(); t.setType(type); t.setAmount(amount); t.setDate(date);
        return t;
    }
}
