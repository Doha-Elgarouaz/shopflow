package com.shopflow.stock.consumer;

import com.shopflow.stock.event.OrderCancelledEvent;
import com.shopflow.stock.event.OrderCreatedEvent;
import com.shopflow.stock.event.StockReservedEvent;
import com.shopflow.stock.event.StockReservedItem;
import com.shopflow.stock.producer.StockEventProducer;
import com.shopflow.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final StockService stockService;
    private final StockEventProducer eventProducer;

    @KafkaListener(topics = "order-events", groupId = "stock-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("Consuming OrderCreatedEvent for orderId: {}", event.orderId());
        
        List<StockReservedItem> reservedItems = event.items().stream().map(item -> {
            stockService.reserveStock(item.productId(), item.quantity(), event.orderId());
            return new StockReservedItem(item.productId(), item.quantity());
        }).collect(Collectors.toList());

        StockReservedEvent reservedEvent = new StockReservedEvent(event.orderId(), reservedItems);
        eventProducer.publishStockReservedEvent(reservedEvent);
    }

    @KafkaListener(topics = "order-events", groupId = "stock-service-group", properties = "spring.json.value.default.type=com.shopflow.stock.event.OrderCancelledEvent")
    public void consumeOrderCancelled(OrderCancelledEvent event) {
        log.info("Consuming OrderCancelledEvent for orderId: {}", event.orderId());
        event.items().forEach(item -> {
            try {
                stockService.releaseStock(item.productId(), item.quantity(), event.orderId());
            } catch (Exception e) {
                log.error("Failed to release stock for product {} in cancelled order {}", item.productId(), event.orderId(), e);
            }
        });
    }
}
