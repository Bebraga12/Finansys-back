package com.finasys.auth.dto;

import com.finasys.common.enums.UserPlan;

public record AuthResponse(
        String token,
        String email,
        String name,
        UserPlan plan
) {}
