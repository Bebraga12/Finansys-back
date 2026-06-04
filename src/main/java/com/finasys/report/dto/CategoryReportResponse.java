package com.finasys.report.dto;

import java.math.BigDecimal;

public record CategoryReportResponse(
        String categoryName,
        BigDecimal total,
        double percentage,
        long transactionCount
) {}
