package com.finasys.plan.dto;

import com.finasys.common.enums.UserPlan;
import jakarta.validation.constraints.NotNull;

public record UpdatePlanRequest(
        @NotNull(message = "Plano é obrigatório") UserPlan plan
) {}
