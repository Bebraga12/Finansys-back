package com.finasys.user.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {

    @Test
    void shouldHaveRequiredFields() {
        List<String> fields = Arrays.stream(User.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        assertThat(fields).contains("id", "name", "email", "passwordHash", "plan", "createdAt", "updatedAt");
    }
}
