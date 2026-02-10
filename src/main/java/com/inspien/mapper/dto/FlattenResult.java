package com.inspien.mapper.dto;

import com.inspien.order.domain.Order;
import java.util.List;

public record FlattenResult(
    List<Order> orders,
    int skippedCount
) {}
