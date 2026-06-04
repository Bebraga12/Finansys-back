package com.finasys.common.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTypeTest {

    @Test
    void shouldHaveIncomeValue() {
        assertThat(TransactionType.valueOf("INCOME")).isEqualTo(TransactionType.INCOME);
    }

    @Test
    void shouldHaveExpenseValue() {
        assertThat(TransactionType.valueOf("EXPENSE")).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    void shouldHaveExactlyTwoValues() {
        assertThat(TransactionType.values()).hasSize(2);
    }
}
