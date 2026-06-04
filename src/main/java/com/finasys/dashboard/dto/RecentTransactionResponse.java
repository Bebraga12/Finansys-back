package com.finasys.dashboard.dto;

import com.finasys.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecentTransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String description,
        String categoryName,
        LocalDate date
) {}
