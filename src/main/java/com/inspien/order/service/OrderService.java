package com.inspien.order.service;

import com.inspien.receiver.jdbc.BatchResult;
import com.inspien.common.exception.ErrorCode;
import com.inspien.order.domain.Order;
import com.inspien.receiver.jdbc.OrderRepository;
import com.inspien.sender.dto.CreateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PlatformTransactionManager txManager;
    private final OrderIdGenerator idGenerator;

    private static final int MAX_RETRY = 3;

    private CreateOrderResult saveOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw ErrorCode.VALIDATION_ERROR.exception();
        }

        TransactionTemplate requiresNewTx = new TransactionTemplate(txManager);
        requiresNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            for (Order o : orders) {
                o.setOrderId(idGenerator.nextOrderId());
            }

            try {
                BatchResult summary = requiresNewTx.execute(status -> {
                    int[] results = orderRepository.batchInsert(orders);
                    return BatchResult.from(results, orders.size());
                });
                return CreateOrderResult.ok(summary, attempt);

            } catch (DuplicateKeyException e) {
                if (attempt == MAX_RETRY) {
                    throw ErrorCode.DUPLICATE_KEY.exception();
                }
            }
        }
        throw ErrorCode.INTERNAL_ERROR.exception();
    }
}
