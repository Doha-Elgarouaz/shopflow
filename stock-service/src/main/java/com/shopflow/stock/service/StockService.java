package com.shopflow.stock.service;

import com.shopflow.stock.dto.StockAdjustmentRequest;
import com.shopflow.stock.dto.StockItemResponse;
import com.shopflow.stock.event.NotificationEvent;
import com.shopflow.stock.model.MovementType;
import com.shopflow.stock.model.StockItem;
import com.shopflow.stock.model.StockMovement;
import com.shopflow.stock.producer.StockEventProducer;
import com.shopflow.stock.repository.StockMovementRepository;
import com.shopflow.stock.repository.StockRepository;
import com.shopflow.stock.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockEventProducer eventProducer;

    @Value("${stock.low-threshold:5}")
    private int lowStockThreshold;

    @Transactional
    public void reserveStock(Long productId, Integer quantity, String orderId) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock item not found for product: " + productId));

        if (item.getAvailableQuantity() < quantity) {
            log.warn("Insufficient stock for product {}, requested: {}, available: {}", productId, quantity, item.getAvailableQuantity());
            // Based on requirements, just log a warning and don't throw exception for demo
        } else {
            item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
            item.setReservedQuantity(item.getReservedQuantity() + quantity);
            stockRepository.save(item);

            StockMovement movement = StockMovement.builder()
                    .productId(productId)
                    .type(MovementType.RESERVATION)
                    .quantity(quantity)
                    .referenceId(orderId)
                    .reason("Reserved for order")
                    .build();
            stockMovementRepository.save(movement);
            
            checkLowStock(item);
        }
    }

    @Transactional
    public void releaseStock(Long productId, Integer quantity, String orderId) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock item not found for product: " + productId));

        item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
        item.setReservedQuantity(item.getReservedQuantity() - quantity);
        stockRepository.save(item);

        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(MovementType.RELEASE)
                .quantity(quantity)
                .referenceId(orderId)
                .reason("Released from order")
                .build();
        stockMovementRepository.save(movement);
    }

    @Transactional
    public StockItemResponse restockProduct(Long productId, Integer quantity) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock item not found for product: " + productId));

        item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
        stockRepository.save(item);

        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(MovementType.RESTOCK)
                .quantity(quantity)
                .reason("Manual restock")
                .build();
        stockMovementRepository.save(movement);

        return mapToResponse(item);
    }

    @Transactional
    public StockItemResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock item not found for product: " + productId));

        item.setAvailableQuantity(item.getAvailableQuantity() + request.getQuantity()); // quantity can be negative
        stockRepository.save(item);

        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(MovementType.ADJUSTMENT)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .build();
        stockMovementRepository.save(movement);

        checkLowStock(item);

        return mapToResponse(item);
    }

    @Transactional
    public StockItemResponse initializeStock(Long productId, String productName, String sku, Integer quantity) {
        StockItem item = stockRepository.findByProductId(productId)
                .orElseGet(() -> StockItem.builder()
                        .productId(productId)
                        .productName(productName)
                        .sku(sku)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());
                        
        item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
        stockRepository.save(item);
        
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(MovementType.RESTOCK)
                .quantity(quantity)
                .reason("Initial stock setup")
                .build();
        stockMovementRepository.save(movement);
        
        return mapToResponse(item);
    }

    @Transactional(readOnly = true)
    public StockItemResponse getStockByProductId(Long productId) {
        return stockRepository.findByProductId(productId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new StockNotFoundException("Stock item not found for product: " + productId));
    }

    @Transactional(readOnly = true)
    public Page<StockItemResponse> getAllStockItems(int page, int size) {
        return stockRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    private void checkLowStock(StockItem item) {
        if (item.getAvailableQuantity() <= lowStockThreshold) {
            log.warn("Stock for product {} is low ({} remaining)", item.getProductId(), item.getAvailableQuantity());
            NotificationEvent notification = new NotificationEvent(
                    "admin@shopflow.com", 
                    "admin-1", 
                    "STOCK_LOW", 
                    "Low Stock Alert - " + item.getProductName(), 
                    "Product " + item.getProductName() + " (SKU: " + item.getSku() + ") is running low. Only " + item.getAvailableQuantity() + " left.",
                    String.valueOf(item.getProductId())
            );
            eventProducer.publishNotificationEvent(notification);
        }
    }

    private StockItemResponse mapToResponse(StockItem item) {
        return StockItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .sku(item.getSku())
                .availableQuantity(item.getAvailableQuantity())
                .reservedQuantity(item.getReservedQuantity())
                .totalQuantity(item.getTotalQuantity())
                .lastUpdated(item.getLastUpdated())
                .build();
    }
}
