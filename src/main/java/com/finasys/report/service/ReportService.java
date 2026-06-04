package com.finasys.report.service;

import com.finasys.common.enums.TransactionType;
import com.finasys.common.enums.UserPlan;
import com.finasys.plan.service.PlanService;
import com.finasys.report.dto.*;
import com.finasys.transaction.dto.TransactionResponse;
import com.finasys.transaction.model.Transaction;
import com.finasys.transaction.repository.TransactionRepository;
import com.finasys.transaction.service.TransactionService;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final PlanService planService;

    public ReportService(TransactionRepository transactionRepository,
                          TransactionService transactionService,
                          UserService userService,
                          PlanService planService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.userService = userService;
        this.planService = planService;
    }

    public MonthlyReportResponse getMonthlyReport(String email, int year, int month) {
        User user = userService.findUser(email);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(user.getId(), start, end);

        BigDecimal income = sum(transactions, TransactionType.INCOME);
        BigDecimal expense = sum(transactions, TransactionType.EXPENSE);

        List<TransactionResponse> responses = transactions.stream()
                .map(transactionService::toResponse).toList();

        return new MonthlyReportResponse(year, month, income, expense, income.subtract(expense), responses);
    }

    public List<CategoryReportResponse> getCategoryReport(String email, int year, int month) {
        User user = userService.findUser(email);
        planService.requirePlan(user, UserPlan.PREMIUM);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(user.getId(), start, end);

        BigDecimal totalExpense = sum(transactions, TransactionType.EXPENSE);

        Map<String, List<Transaction>> grouped = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCategory() != null)
                .collect(Collectors.groupingBy(t -> t.getCategory().getName()));

        return grouped.entrySet().stream()
                .map(e -> {
                    BigDecimal total = e.getValue().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    double pct = totalExpense.compareTo(BigDecimal.ZERO) == 0 ? 0
                            : total.divide(totalExpense, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
                    return new CategoryReportResponse(e.getKey(), total, pct, e.getValue().size());
                })
                .toList();
    }

    public MonthlyComparisonResponse getMonthlyComparison(String email, int year, int month) {
        User user = userService.findUser(email);
        planService.requirePlan(user, UserPlan.PREMIUM_PLUS);

        LocalDate currentStart = LocalDate.of(year, month, 1);
        LocalDate currentEnd = currentStart.withDayOfMonth(currentStart.lengthOfMonth());
        LocalDate prevStart = currentStart.minusMonths(1);
        LocalDate prevEnd = prevStart.withDayOfMonth(prevStart.lengthOfMonth());

        List<Transaction> current = transactionRepository.findByUserIdAndDateBetween(user.getId(), currentStart, currentEnd);
        List<Transaction> previous = transactionRepository.findByUserIdAndDateBetween(user.getId(), prevStart, prevEnd);

        BigDecimal curIncome = sum(current, TransactionType.INCOME);
        BigDecimal curExpense = sum(current, TransactionType.EXPENSE);
        BigDecimal prevIncome = sum(previous, TransactionType.INCOME);
        BigDecimal prevExpense = sum(previous, TransactionType.EXPENSE);

        return new MonthlyComparisonResponse(year, month, curIncome, curExpense, prevIncome, prevExpense,
                curIncome.subtract(prevIncome), curExpense.subtract(prevExpense));
    }

    public InsightResponse getInsights(String email) {
        User user = userService.findUser(email);
        planService.requirePlan(user, UserPlan.PREMIUM_PLUS);

        List<Transaction> last30 = transactionRepository.findByUserIdAndDateBetween(
                user.getId(), LocalDate.now().minusDays(30), LocalDate.now());
        List<Transaction> prev30 = transactionRepository.findByUserIdAndDateBetween(
                user.getId(), LocalDate.now().minusDays(60), LocalDate.now().minusDays(31));

        BigDecimal recentExpense = sum(last30, TransactionType.EXPENSE);
        BigDecimal prevExpense = sum(prev30, TransactionType.EXPENSE);

        List<String> insights = new ArrayList<>();

        if (prevExpense.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal variation = recentExpense.subtract(prevExpense)
                    .divide(prevExpense, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            if (variation.compareTo(BigDecimal.TEN) > 0) {
                insights.add(String.format("Seus gastos aumentaram %.1f%% nos últimos 30 dias.", variation.doubleValue()));
            } else if (variation.compareTo(new BigDecimal("-10")) < 0) {
                insights.add(String.format("Parabéns! Seus gastos reduziram %.1f%% nos últimos 30 dias.", variation.abs().doubleValue()));
            }
        }

        if (insights.isEmpty()) {
            insights.add("Seus gastos estão estáveis. Continue assim!");
        }

        return new InsightResponse(insights);
    }

    private BigDecimal sum(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
