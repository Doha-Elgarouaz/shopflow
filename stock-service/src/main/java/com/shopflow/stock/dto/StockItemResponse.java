package com.shopflow.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer totalQuantity;
    private LocalDateTime lastUpdated;
}
