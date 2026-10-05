package com.shopflow.stock.service;

import com.shopflow.stock.dto.StockAdjustmentRequest;
import com.shopflow.stock.dto.StockItemResponse;
import com.shopflow.stock.exception.StockNotFoundException;
import com.shopflow.stock.model.MovementType;
import com.shopflow.stock.model.StockItem;
import com.shopflow.stock.model.StockMovement;
import com.shopflow.stock.producer.StockEventProducer;
import com.shopflow.stock.repository.StockMovementRepository;
import com.shopflow.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService Unit Tests")
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private StockEventProducer eventProducer;

    @InjectMocks
    private StockService stockService;

    private StockItem sampleStock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stockService, "lowStockThreshold", 5);

        sampleStock = StockItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .sku("TEST-SKU-100")
                .availableQuantity(20)
                .reservedQuantity(0)
                .build();
    }

    @Test
    @DisplayName("reserveStock — should decrement available and increment reserved quantity")
    void reserveStock_Success() {
        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(sampleStock));

        stockService.reserveStock(100L, 5, "ORD-123");

        assertThat(sampleStock.getAvailableQuantity()).isEqualTo(15);
        assertThat(sampleStock.getReservedQuantity()).isEqualTo(5);
        verify(stockRepository).save(sampleStock);
        verify(stockMovementRepository).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("reserveStock — should throw StockNotFoundException when product not found")
    void reserveStock_NotFound() {
        when(stockRepository.findByProductId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.reserveStock(999L, 5, "ORD-123"))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @DisplayName("releaseStock — should increment available and decrement reserved quantity")
    void releaseStock_Success() {
        sampleStock.setAvailableQuantity(15);
        sampleStock.setReservedQuantity(5);
        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(sampleStock));

        stockService.releaseStock(100L, 5, "ORD-123");

        assertThat(sampleStock.getAvailableQuantity()).isEqualTo(20);
        assertThat(sampleStock.getReservedQuantity()).isEqualTo(0);
        verify(stockRepository).save(sampleStock);
        verify(stockMovementRepository).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("restockProduct — should increase available stock")
    void restockProduct_Success() {
        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(sampleStock));

        StockItemResponse response = stockService.restockProduct(100L, 10);

        assertThat(sampleStock.getAvailableQuantity()).isEqualTo(30);
        assertThat(response.getAvailableQuantity()).isEqualTo(30);
        verify(stockRepository).save(sampleStock);
    }

    @Test
    @DisplayName("adjustStock — should adjust stock with positive or negative quantity")
    void adjustStock_Success() {
        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setQuantity(-3);
        request.setReason("Damaged inventory");

        when(stockRepository.findByProductId(100L)).thenReturn(Optional.of(sampleStock));

        StockItemResponse response = stockService.adjustStock(100L, request);

        assertThat(sampleStock.getAvailableQuantity()).isEqualTo(17);
        assertThat(response.getAvailableQuantity()).isEqualTo(17);
        verify(stockRepository).save(sampleStock);
    }
}
