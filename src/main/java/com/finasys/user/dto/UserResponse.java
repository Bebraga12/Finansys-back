package com.finasys.user.dto;

import com.finasys.common.enums.UserPlan;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserPlan plan,
        LocalDateTime createdAt
) {}
