package com.finasys.dashboard.service;

import com.finasys.common.enums.TransactionType;
import com.finasys.common.enums.UserPlan;
import com.finasys.dashboard.dto.DashboardSummaryResponse;
import com.finasys.dashboard.dto.ExpenseByCategoryResponse;
import com.finasys.dashboard.dto.RecentTransactionResponse;
import com.finasys.transaction.model.Transaction;
import com.finasys.transaction.repository.TransactionRepository;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public DashboardService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public DashboardSummaryResponse getSummary(String email) {
        User user = userService.findUser(email);
        List<Transaction> all = transactionRepository.findByUserId(user.getId());

        LocalDate now = LocalDate.now();
        List<Transaction> thisMonth = all.stream()
                .filter(t -> t.getDate().getYear() == now.getYear() && t.getDate().getMonthValue() == now.getMonthValue())
                .toList();

        BigDecimal balance = all.stream()
                .map(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyIncome = thisMonth.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpense = thisMonth.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RecentTransactionResponse> recent = all.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(10)
                .map(t -> new RecentTransactionResponse(t.getId(), t.getType(), t.getAmount(),
                        t.getDescription(),
                        t.getCategory() != null ? t.getCategory().getName() : null,
                        t.getDate()))
                .toList();

        boolean isPremium = user.getPlan() != UserPlan.BASIC;

        List<ExpenseByCategoryResponse> byCategory = isPremium
                ? buildExpensesByCategory(thisMonth, monthlyExpense)
                : List.of();

        return new DashboardSummaryResponse(balance, monthlyIncome, monthlyExpense, recent, byCategory,
                user.getPlan(), !isPremium);
    }

    private List<ExpenseByCategoryResponse> buildExpensesByCategory(List<Transaction> transactions, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return List.of();

        Map<String, BigDecimal> grouped = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        return grouped.entrySet().stream()
                .map(e -> new ExpenseByCategoryResponse(e.getKey(), e.getValue(),
                        e.getValue().divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue()))
                .toList();
    }
}
