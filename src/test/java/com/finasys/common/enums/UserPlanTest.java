package com.finasys.common.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPlanTest {

    @Test
    void shouldHaveBasicValue() {
        assertThat(UserPlan.valueOf("BASIC")).isEqualTo(UserPlan.BASIC);
    }

    @Test
    void shouldHavePremiumValue() {
        assertThat(UserPlan.valueOf("PREMIUM")).isEqualTo(UserPlan.PREMIUM);
    }

    @Test
    void shouldHavePremiumPlusValue() {
        assertThat(UserPlan.valueOf("PREMIUM_PLUS")).isEqualTo(UserPlan.PREMIUM_PLUS);
    }

    @Test
    void shouldHaveExactlyThreeValues() {
        assertThat(UserPlan.values()).hasSize(3);
    }
}
