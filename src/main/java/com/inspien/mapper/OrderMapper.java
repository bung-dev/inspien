package com.inspien.mapper;

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

    @Value("${application_key:}")
    String applicationKey;

    public List<Order> flatten(OrderRequestXML req) {
        if (req == null) {
            return List.of();
        }

        List<OrderHeaderXml> headers = req.getHeaders();
        List<OrderItemXml> items = req.getItems();

        Map<String, OrderHeaderXml> headerMap = headers.stream()
                .filter(h -> !h.getUserId().isBlank())
                .collect(Collectors.toMap(
                        OrderHeaderXml::getUserId,
                        Function.identity(),
                        (a, b) -> a
                ));


        List<Order> result = new ArrayList<>(items.size());

        for (OrderItemXml item : items) {
            String userId = item.getUserId();

            if (userId.isBlank()) {
                continue;
            }

            OrderHeaderXml header = headerMap.get(userId);
            if (header == null) {
                continue;
            }

            result.add(Order.builder()
                    .userId(userId)
                    .itemId(item.getItemId())
                    .itemName(item.getItemName())
                    .price(item.getPrice())
                    .name(header.getName())
                    .address(header.getAddress())
                    .status(header.getStatus())
                    .applicantKey(applicationKey)
                    .build());
        }
        return result;
    }

}
