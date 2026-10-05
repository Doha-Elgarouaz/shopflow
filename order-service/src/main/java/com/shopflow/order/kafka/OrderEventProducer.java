package com.shopflow.order.kafka;

import com.shopflow.order.kafka.event.OrderCancelledEvent;
import com.shopflow.order.kafka.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private static final String ORDER_EVENTS_TOPIC = "order-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for order: {}", event.orderNumber());
        kafkaTemplate.send(ORDER_EVENTS_TOPIC, event.orderNumber(), event);
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("Publishing OrderCancelledEvent for order: {}", event.orderNumber());
        kafkaTemplate.send(ORDER_EVENTS_TOPIC, event.orderNumber(), event);
    }
}
