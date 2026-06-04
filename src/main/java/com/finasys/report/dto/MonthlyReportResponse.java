package com.finasys.report.dto;

import com.finasys.transaction.dto.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyReportResponse(
        int year,
        int month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<TransactionResponse> transactions
) {}
