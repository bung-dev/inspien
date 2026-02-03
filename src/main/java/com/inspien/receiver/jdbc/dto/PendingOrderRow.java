package com.inspien.receiver.jdbc.dto;

public record PendingOrderRow(
        String orderId,
        String itemId,
        String applicantKey,
        String address
) {}
