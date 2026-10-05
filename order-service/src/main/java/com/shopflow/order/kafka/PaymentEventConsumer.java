package com.shopflow.order.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.entity.OrderStatus;
import com.shopflow.order.kafka.event.PaymentCompletedEvent;
import com.shopflow.order.kafka.event.PaymentFailedEvent;
import com.shopflow.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    @Transactional
    public void consume(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);

            // Detect event type by fields present
            if (node.has("transactionId") && !node.get("transactionId").isNull()) {
                PaymentCompletedEvent event = objectMapper.treeToValue(node, PaymentCompletedEvent.class);
                handlePaymentCompleted(event);
            } else if (node.has("reason")) {
                PaymentFailedEvent event = objectMapper.treeToValue(node, PaymentFailedEvent.class);
                handlePaymentFailed(event);
            } else {
                log.warn("Unknown payment event structure: {}", message);
            }
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("PaymentCompleted received for orderId={}, transactionId={}", event.orderId(), event.transactionId());
        orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            log.info("Order {} status updated to PAID", order.getOrderNumber());
        }, () -> log.warn("Order {} not found for PaymentCompleted event", event.orderId()));
    }

    private void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("PaymentFailed received for orderId={}, reason={}", event.orderId(), event.reason());
        orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Order {} status updated to CANCELLED due to payment failure", order.getOrderNumber());
        }, () -> log.warn("Order {} not found for PaymentFailed event", event.orderId()));
    }
}
