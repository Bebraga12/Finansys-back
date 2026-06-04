package com.finasys.goal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline,
        double progressPercent
) {}
