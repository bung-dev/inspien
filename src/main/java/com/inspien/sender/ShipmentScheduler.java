package com.inspien.sender;

import com.inspien.order.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentScheduler {
    private final ShipmentService shipmentService;

    @Value("${application.key}")
    private String applicationKey;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void run() {
        shipmentService.run(applicationKey);
    }
}
