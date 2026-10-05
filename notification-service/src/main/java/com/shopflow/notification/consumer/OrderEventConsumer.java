package com.shopflow.notification.consumer;

import com.shopflow.notification.event.OrderCancelledEvent;
import com.shopflow.notification.event.OrderCreatedEvent;
import com.shopflow.notification.model.NotificationType;
import com.shopflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("Consuming OrderCreatedEvent for orderId: {}", event.orderId());
        String subject = "Order Confirmed - " + event.orderNumber();
        String content = "Your order " + event.orderNumber() + " with total amount " + event.totalAmount() + " has been successfully created.";
        notificationService.saveAndSendNotification(event.customerEmail(), event.customerId(), NotificationType.ORDER_CONFIRMED, subject, content, event.orderId());
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service-group", properties = "spring.json.value.default.type=com.shopflow.notification.event.OrderCancelledEvent")
    public void consumeOrderCancelled(OrderCancelledEvent event) {
        log.info("Consuming OrderCancelledEvent for orderId: {}", event.orderId());
        String subject = "Order Cancelled - " + event.orderNumber();
        String content = "Your order " + event.orderNumber() + " has been cancelled. Reason: " + event.reason();
        notificationService.saveAndSendNotification(event.customerEmail(), event.customerId(), NotificationType.ORDER_CANCELLED, subject, content, event.orderId());
    }
}
