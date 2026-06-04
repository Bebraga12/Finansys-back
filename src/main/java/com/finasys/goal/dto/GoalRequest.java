package com.finasys.goal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
        @NotBlank(message = "Título é obrigatório") String title,
        @NotNull @Positive(message = "Valor objetivo deve ser positivo") BigDecimal targetAmount,
        @NotNull @PositiveOrZero(message = "Valor atual deve ser maior ou igual a zero") BigDecimal currentAmount,
        @NotNull(message = "Prazo é obrigatório") LocalDate deadline
) {}
