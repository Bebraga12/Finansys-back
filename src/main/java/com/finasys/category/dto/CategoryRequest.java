package com.finasys.category.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        Boolean incomeEnabled,
        Boolean expenseEnabled
) {
    @AssertTrue(message = "Pelo menos um tipo deve ser selecionado: receita ou despesa")
    public boolean isAtLeastOneEnabled() {
        return Boolean.TRUE.equals(incomeEnabled) || Boolean.TRUE.equals(expenseEnabled);
    }
}
