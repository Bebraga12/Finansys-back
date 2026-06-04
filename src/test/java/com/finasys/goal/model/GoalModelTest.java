package com.finasys.goal.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GoalModelTest {

    @Test
    void shouldHaveRequiredFields() {
        List<String> fields = Arrays.stream(Goal.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        assertThat(fields).contains(
                "id", "user", "title", "targetAmount", "currentAmount", "deadline", "createdAt", "updatedAt"
        );
    }
}
