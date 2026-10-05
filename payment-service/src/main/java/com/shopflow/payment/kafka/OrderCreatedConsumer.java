package com.shopflow.payment.kafka;

import com.shopflow.payment.kafka.event.OrderCreatedEvent;
import com.shopflow.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void consume(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent received — orderId={}, amount={}, customer={}",
                event.orderId(), event.totalAmount(), event.customerId());
        try {
            paymentService.processPaymentForOrder(event);
        } catch (Exception e) {
            log.error("Failed to process payment for order {}: {}", event.orderId(), e.getMessage(), e);
        }
    }
}
