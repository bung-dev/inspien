package com.inspien.receiver.jdbc.dto;

public record ShipmentRow(
        String shipmentId,
        String orderId,
        String itemId,
        String applicantKey,
        String address
) {}
