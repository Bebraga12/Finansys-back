package com.finasys.report.service;

import com.finasys.common.enums.TransactionType;
import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.InsufficientPlanException;
import com.finasys.plan.service.PlanService;
import com.finasys.report.dto.MonthlyReportResponse;
import com.finasys.transaction.model.Transaction;
import com.finasys.transaction.repository.TransactionRepository;
import com.finasys.transaction.service.TransactionService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionService transactionService;
    @Mock private UserService userService;
    @Mock private PlanService planService;
    @InjectMocks private ReportService reportService;

    @Test
    void basicUserCanAccessMonthlyReport() {
        User user = buildUser(UserPlan.BASIC);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserIdAndDateBetween(any(UUID.class), any(), any())).thenReturn(List.of());

        MonthlyReportResponse report = reportService.getMonthlyReport("user@example.com", 2026, 6);

        assertThat(report.year()).isEqualTo(2026);
        assertThat(report.month()).isEqualTo(6);
    }

    @Test
    void shouldCalculateMonthlyTotals() {
        User user = buildUser(UserPlan.BASIC);
        LocalDate date = LocalDate.of(2026, 6, 15);
        List<Transaction> transactions = List.of(
                buildTransaction(TransactionType.INCOME, new BigDecimal("3000"), date),
                buildTransaction(TransactionType.EXPENSE, new BigDecimal("800"), date)
        );
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserIdAndDateBetween(any(UUID.class), any(), any())).thenReturn(transactions);
        when(transactionService.toResponse(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            return new com.finasys.transaction.dto.TransactionResponse(null, t.getType(), t.getAmount(), null, null, t.getDate());
        });

        MonthlyReportResponse report = reportService.getMonthlyReport("user@example.com", 2026, 6);

        assertThat(report.totalIncome()).isEqualByComparingTo("3000");
        assertThat(report.totalExpense()).isEqualByComparingTo("800");
        assertThat(report.balance()).isEqualByComparingTo("2200");
    }

    @Test
    void basicUserCannotAccessMonthlyComparison() {
        User user = buildUser(UserPlan.BASIC);
        when(userService.findUser("user@example.com")).thenReturn(user);
        doThrow(InsufficientPlanException.class).when(planService).requirePlan(user, UserPlan.PREMIUM_PLUS);

        assertThatThrownBy(() -> reportService.getMonthlyComparison("user@example.com", 2026, 6))
                .isInstanceOf(InsufficientPlanException.class);
    }

    @Test
    void premiumUserCannotAccessInsights() {
        User user = buildUser(UserPlan.PREMIUM);
        when(userService.findUser("user@example.com")).thenReturn(user);
        doThrow(InsufficientPlanException.class).when(planService).requirePlan(user, UserPlan.PREMIUM_PLUS);

        assertThatThrownBy(() -> reportService.getInsights("user@example.com"))
                .isInstanceOf(InsufficientPlanException.class);
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
