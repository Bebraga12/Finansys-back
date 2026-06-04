package com.finasys.category.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryModelTest {

    @Test
    void shouldHaveRequiredFields() {
        List<String> fields = Arrays.stream(Category.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        assertThat(fields).contains("id", "name", "incomeEnabled", "expenseEnabled", "user", "createdAt", "updatedAt");
    }
}
