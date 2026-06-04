package com.finasys.plan.dto;

import com.finasys.common.enums.UserPlan;

import java.util.List;

public record PlanInfoResponse(
        UserPlan plan,
        String displayName,
        List<String> features
) {}
