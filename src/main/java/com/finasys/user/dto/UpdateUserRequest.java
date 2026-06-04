package com.finasys.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Nome é obrigatório") String name
) {}
