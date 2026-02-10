package com.inspien.sender.dto;


public record CreateOrderResult(
        boolean success,
        String message,
        int orderCount,
        int skippedCount,
        int retryCount
) {
    public static CreateOrderResult ok(int orderCount, int skippedCount, int retryCount) {
        return new CreateOrderResult(true, "주문 처리에 성공하였습니다.", orderCount, skippedCount, retryCount);
    }

    public static CreateOrderResult fail(String message) {
        return new CreateOrderResult(false, message, 0, 0, 0);
    }
}