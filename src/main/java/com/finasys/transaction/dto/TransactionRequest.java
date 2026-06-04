package com.finasys.transaction.dto;

import com.finasys.common.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = "Tipo é obrigatório") TransactionType type,
        @NotNull(message = "Valor é obrigatório") @Positive(message = "Valor deve ser positivo") BigDecimal amount,
        UUID categoryId,
        String description,
        @NotNull(message = "Data é obrigatória") LocalDate date
) {}
