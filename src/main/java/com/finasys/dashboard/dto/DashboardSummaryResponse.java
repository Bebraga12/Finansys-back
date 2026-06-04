package com.finasys.dashboard.dto;

import com.finasys.common.enums.UserPlan;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal currentBalance,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpense,
        List<RecentTransactionResponse> recentTransactions,
        List<ExpenseByCategoryResponse> expensesByCategory,
        UserPlan currentPlan,
        boolean hasLockedFeatures
) {}
