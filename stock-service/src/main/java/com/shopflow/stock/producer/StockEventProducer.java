package com.shopflow.stock.producer;

import com.shopflow.stock.event.NotificationEvent;
import com.shopflow.stock.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStockReservedEvent(StockReservedEvent event) {
        log.info("Publishing StockReservedEvent for orderId: {}", event.orderId());
        kafkaTemplate.send("stock-events", event.orderId(), event);
    }
    
    public void publishNotificationEvent(NotificationEvent event) {
        log.info("Publishing NotificationEvent for low stock");
        kafkaTemplate.send("notification-events", event.referenceId(), event);
    }
}
