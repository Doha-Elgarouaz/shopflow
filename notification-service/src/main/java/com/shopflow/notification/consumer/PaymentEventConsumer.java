package com.shopflow.notification.consumer;

import com.shopflow.notification.event.PaymentCompletedEvent;
import com.shopflow.notification.event.PaymentFailedEvent;
import com.shopflow.notification.model.NotificationType;
import com.shopflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "payment-events", groupId = "notification-service-group")
    public void consumePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Consuming PaymentCompletedEvent for paymentId: {}", event.paymentId());
        String subject = "Payment Successful - " + event.orderId();
        String content = "Your payment of " + event.amount() + " for order " + event.orderId() + " was successful.";
        notificationService.saveAndSendNotification(event.customerEmail(), event.customerId(), NotificationType.PAYMENT_SUCCESS, subject, content, event.paymentId());
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service-group", properties = "spring.json.value.default.type=com.shopflow.notification.event.PaymentFailedEvent")
    public void consumePaymentFailed(PaymentFailedEvent event) {
        log.info("Consuming PaymentFailedEvent for paymentId: {}", event.paymentId());
        String subject = "Payment Failed - " + event.orderId();
        String content = "Your payment for order " + event.orderId() + " failed due to: " + event.reason();
        notificationService.saveAndSendNotification(event.customerEmail(), event.customerId(), NotificationType.PAYMENT_FAILED, subject, content, event.paymentId());
    }
}
