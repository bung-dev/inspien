package com.inspien.order.service;

import com.inspien.receiver.jdbc.BatchResult;
import com.inspien.receiver.sftp.FileWriter;
import com.inspien.receiver.sftp.SftpUploader;
import com.inspien.common.exception.ErrorCode;
import com.inspien.mapper.OrderMapper;
import com.inspien.mapper.OrderParserXML;
import com.inspien.mapper.OrderRequestValidator;
import com.inspien.mapper.dto.OrderRequestXML;
import com.inspien.order.domain.Order;
import com.inspien.receiver.jdbc.OrderRepository;
import com.inspien.sender.dto.CreateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Path;
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

        TransactionTemplate requiresNewTx = requiresNewTemplate();

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            assignOrderId(orders);

            try {
                BatchResult summary = executeBatchInsert(requiresNewTx, orders);
                return CreateOrderResult.ok(summary, attempt);

            } catch (DuplicateKeyException e) {
                if (attempt == MAX_RETRY) {
                    throw ErrorCode.DUPLICATE_KEY.exception();
                }
            }
        }
        throw ErrorCode.INTERNAL_ERROR.exception();
    }

    private TransactionTemplate requiresNewTemplate() {
        TransactionTemplate tx = new TransactionTemplate(txManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return tx;
    }

    private BatchResult executeBatchInsert(TransactionTemplate tx, List<Order> orders) {
        return tx.execute(status -> {
            int[] result = orderRepository.batchInsert(orders);
            return BatchResult.from(result, orders.size());
        });
    }

    private void assignOrderId(List<Order> orders) {
        for (Order o : orders) {
            o.setOrderId(idGenerator.nextOrderId());
        }
    }
}
