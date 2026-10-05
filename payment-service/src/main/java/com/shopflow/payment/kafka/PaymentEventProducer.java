package com.shopflow.payment.kafka;

import com.shopflow.payment.kafka.event.PaymentCompletedEvent;
import com.shopflow.payment.kafka.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Publishing PaymentCompletedEvent for orderId={}, transactionId={}",
                event.orderId(), event.transactionId());
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, String.valueOf(event.orderId()), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        log.warn("Publishing PaymentFailedEvent for orderId={}, reason={}",
                event.orderId(), event.reason());
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, String.valueOf(event.orderId()), event);
    }
}
