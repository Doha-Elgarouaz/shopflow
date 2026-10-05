package com.shopflow.product.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "product-events";

    public void publishStockUpdate(Long productId, Integer newQuantity) {
        Map<String, Object> event = new HashMap<>();
        event.put("productId", productId);
        event.put("newQuantity", newQuantity);
        event.put("eventType", "STOCK_UPDATE");
        
        log.info("Publishing stock update event for product {}: {}", productId, newQuantity);
        kafkaTemplate.send(TOPIC, productId.toString(), event);
    }
}
