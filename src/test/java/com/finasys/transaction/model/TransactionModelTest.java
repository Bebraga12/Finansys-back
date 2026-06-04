package com.finasys.transaction.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionModelTest {

    @Test
    void shouldHaveRequiredFields() {
        List<String> fields = Arrays.stream(Transaction.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        assertThat(fields).contains(
                "id", "user", "category", "type", "amount",
                "description", "date", "createdAt", "updatedAt"
        );
    }
}
