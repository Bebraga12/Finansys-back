package com.finasys.report.dto;

import java.math.BigDecimal;

public record MonthlyComparisonResponse(
        int year,
        int month,
        BigDecimal currentIncome,
        BigDecimal currentExpense,
        BigDecimal previousIncome,
        BigDecimal previousExpense,
        BigDecimal incomeDifference,
        BigDecimal expenseDifference
) {}
