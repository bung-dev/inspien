package com.inspien.sender.dto;


import com.inspien.receiver.jdbc.BatchResult;

public record CreateOrderResult(
        boolean success,
        String code,
        String message,
        int attempt,
        BatchResult batch
) {
    public static CreateOrderResult ok(BatchResult batch, int attempt) {
        return new CreateOrderResult(true, "OK", "batch insert success", attempt, batch);
    }
}