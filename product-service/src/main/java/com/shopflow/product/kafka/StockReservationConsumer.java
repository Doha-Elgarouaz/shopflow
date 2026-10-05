package com.shopflow.product.kafka;

import com.shopflow.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockReservationConsumer {

    private final ProductService productService;

    public record StockEvent(Long productId, Integer quantity, String eventType, String orderId) {}

    @KafkaListener(topics = "stock-events", groupId = "product-service-group")
    public void consumeStockReservation(StockEvent event) {
        log.info("Received stock event: {}", event);
        if ("RESERVE_STOCK".equals(event.eventType())) {
            try {
                // Decrement stock by the reserved quantity
                productService.updateStock(event.productId(), -event.quantity());
                log.info("Successfully reserved stock for product {}", event.productId());
            } catch (Exception e) {
                log.error("Failed to process stock reservation for product {}: {}", event.productId(), e.getMessage());
                // In a real system, you would send a failure compensation event here
            }
        }
    }
}
