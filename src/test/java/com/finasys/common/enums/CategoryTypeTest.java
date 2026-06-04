package com.finasys.common.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTypeTest {

    @Test
    void shouldHaveIncomeValue() {
        assertThat(CategoryType.valueOf("INCOME")).isEqualTo(CategoryType.INCOME);
    }

    @Test
    void shouldHaveExpenseValue() {
        assertThat(CategoryType.valueOf("EXPENSE")).isEqualTo(CategoryType.EXPENSE);
    }

    @Test
    void shouldHaveExactlyTwoValues() {
        assertThat(CategoryType.values()).hasSize(2);
    }
}
