package com.inspien.mapper;

import com.inspien.mapper.dto.FlattenResult;
import com.inspien.mapper.dto.OrderHeaderXml;
import com.inspien.mapper.dto.OrderItemXml;
import com.inspien.mapper.dto.OrderRequestXML;
import com.inspien.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderMapper {

    @Value("${application.key:}")
    private String applicationKey;

    public FlattenResult flatten(OrderRequestXML req) {
        if (req == null) {
            return new FlattenResult(List.of(), 0);
        }

        List<OrderHeaderXml> headers = req.getHeaders();
        List<OrderItemXml> items = req.getItems();

        Map<String, OrderHeaderXml> headerMap = headers.stream()
                .filter(h -> h.getUserId() != null && !h.getUserId().isBlank())
                .collect(Collectors.toMap(
                        h -> h.getUserId().trim(),
                        Function.identity(),
                        (a, b) -> a
                ));


        List<Order> orders = new ArrayList<>(items.size());
        int skippedCount = 0;

        for (OrderItemXml item : items) {
            String userId = item.getUserId();

            if (userId == null || userId.isBlank()) {
                skippedCount++;
                continue;
            }

            String trimmedUserId = userId.trim();
            OrderHeaderXml header = headerMap.get(trimmedUserId);
            if (header == null) {
                log.warn("[MAPPER] header_not_found userId={}", trimmedUserId);
                skippedCount++;
                continue;
            }

            orders.add(Order.builder()
                    .userId(trimmedUserId)
                    .itemId(item.getItemId() != null ? item.getItemId().trim() : null)
                    .itemName(item.getItemName())
                    .price(item.getPrice())
                    .name(header.getName())
                    .address(header.getAddress())
                    .status(header.getStatus())
                    .applicantKey(applicationKey)
                    .build());
        }
        return new FlattenResult(orders, skippedCount);
    }

}
