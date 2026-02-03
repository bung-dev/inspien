package com.inspien.order.service;

import com.inspien.receiver.jdbc.OrderRepository;
import com.inspien.receiver.jdbc.ShipmentRepository;
import com.inspien.receiver.jdbc.dto.PendingOrderRow;
import com.inspien.receiver.jdbc.dto.ShipmentRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public void run(String applicantKey) {
        List<PendingOrderRow> pending = orderRepository.findPendingForShipment(applicantKey);
        if (pending.isEmpty()) {
            log.info("[BATCH] pending=0 applicantKey={}", applicantKey);
            return;
        }

        List<ShipmentRow> shipmentRows = pending.stream()
                .map(p -> new ShipmentRow(
                        idGenerator.generate(),
                        p.orderId(),
                        p.itemId(),
                        p.applicantKey(),
                        p.address()
                ))
                .toList();

        int[] results = shipmentRepository.batchInsert(shipmentRows);

        List<String> successOrderIds = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            int r = results[i];
            if (r == 1 || r == -2) {
                successOrderIds.add(shipmentRows.get(i).orderId());
            }
        }

        int updated = orderRepository.updateStatusToY(applicantKey, successOrderIds);
        log.info("[BATCH] pending={}, inserted={}, updated={}",
                pending.size(), successOrderIds.size(), updated);
    }
}
