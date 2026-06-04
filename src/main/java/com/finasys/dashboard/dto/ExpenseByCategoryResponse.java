package com.finasys.dashboard.dto;

import java.math.BigDecimal;

public record ExpenseByCategoryResponse(
        String categoryName,
        BigDecimal total,
        double percentage
) {}
