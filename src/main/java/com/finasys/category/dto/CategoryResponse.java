package com.finasys.category.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        boolean incomeEnabled,
        boolean expenseEnabled
) {}
