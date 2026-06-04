package com.finasys.transaction.dto;

import com.finasys.category.dto.CategoryResponse;
import com.finasys.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        CategoryResponse category,
        String description,
        LocalDate date
) {}
