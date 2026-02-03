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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PlatformTransactionManager txManager;
    private final OrderParserXML orderParserXML;
    private final OrderRequestValidator validator;
    private final OrderMapper mapper;
    private final IdGenerator idGenerator;
    private final FileWriter fileWriter;
    private final SftpUploader sftpUploader;

    private final int MAX_RETRY = 5;
    private  String NAME = "이중호";

    public CreateOrderResult createOrderSync(String base64Xml) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        try {
            log.info("[ORDER] start base64Size={}",
                    base64Xml == null ? 0 : base64Xml.length());

            final OrderRequestXML request;
            try {
                request = orderParserXML.parse(base64Xml);
            } catch (Exception e) {
                log.error("[ORDER] xml_parse_fail", e);
                throw ErrorCode.XML_PARSE_ERROR.exception();
            }

            validator.validate(request);
            log.info("[ORDER] validate_ok");

            List<Order> orders = mapper.flatten(request);
            log.info("[ORDER] flatten_ok orders={}", orders.size());

            CreateOrderResult result = saveOrders(orders);

            log.info("[ORDER] done success={} code={} attempt={} requestedRows={} successCount={} successKnownRows={} successUnknown={} zeroOrOther={}",
                    result.success(),
                    result.code(),
                    result.attempt(),
                    result.batch().requestedRows(),
                    result.batch().successCount(),
                    result.batch().successKnownRows(),
                    result.batch().successUnknown(),
                    result.batch().zeroOrOther()
            );

            return result;
        } finally {
            MDC.remove("traceId");
        }
    }

    private CreateOrderResult saveOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw ErrorCode.VALIDATION_ERROR.exception();
        }

        TransactionTemplate requiresNewTx = requiresNewTemplate();

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            final int attemptNum = attempt;
            assignOrderId(orders);

            try {
                return requiresNewTx.execute(status -> {
                    BatchResult summary = executeBatchInsert(orders);
                    log.info("[ORDER:DB] insert_done attempt={} requestedRows={} successCount={} successKnownRows={} successUnknown={} zeroOrOther={}",
                            attemptNum,
                            summary.requestedRows(),
                            summary.successCount(),
                            summary.successKnownRows(),
                            summary.successUnknown(),
                            summary.zeroOrOther());
                    Path file = fileWriter.write(orders, NAME);
                    log.info("[ORDER:FILE] created file={}", file.getFileName());
                    try {
                        sftpUploader.upload(file);
                        log.info("[ORDER:SFTP] upload_ok file={}", file.getFileName());
                    } catch (RuntimeException e) {
                        log.error("[ORDER:SFTP] upload_fail file={}", file.getFileName(), e);
                        try {
                            Files.deleteIfExists(file);
                            log.info("[ORDER:FILE] deleted file={}", file.getFileName());
                        } catch (Exception deleteEx) {
                            log.warn("[ORDER:FILE] delete_fail file={}", file.getFileName(), deleteEx);
                        }
                        throw ErrorCode.SFTP_SEND_FAIL.exception();
                    }
                    return CreateOrderResult.ok(summary, attemptNum);
                });

            } catch (DuplicateKeyException e) {
                if (attempt == MAX_RETRY) {
                    log.warn("[ORDER:DB] duplicate_key_retry attempt={}/{}", attempt, MAX_RETRY);
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

    private BatchResult executeBatchInsert(List<Order> orders) {
        int[] result = orderRepository.batchInsert(orders);
        return BatchResult.from(result, orders.size());
    }

    private void assignOrderId(List<Order> orders) {
        for (Order o : orders) {
            o.setOrderId(idGenerator.generate());
        }
    }
}
