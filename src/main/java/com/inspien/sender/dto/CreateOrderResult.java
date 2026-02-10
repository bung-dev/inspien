package com.inspien.sender.dto;


import com.inspien.receiver.jdbc.BatchResult;

public record CreateOrderResult(
        boolean success,
        String message,
        int orderCount,
        int retryCount
) {
    public static CreateOrderResult ok(int orderCount, int retryCount) {
        return new CreateOrderResult(true, "주문 처리가 완료되었습니다.", orderCount, retryCount);
    }

    public static CreateOrderResult fail(String message) {
        return new CreateOrderResult(false, message, 0, 0);
    }
}