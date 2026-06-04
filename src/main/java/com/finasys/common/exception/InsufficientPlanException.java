package com.finasys.common.exception;

public class InsufficientPlanException extends RuntimeException {
    public InsufficientPlanException() {
        super("Este recurso está disponível apenas em um plano superior.");
    }
}
