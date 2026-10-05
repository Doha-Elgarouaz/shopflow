package com.shopflow.stock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long productId;

    private String productName;
    private String sku;

    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer totalQuantity; // computed

    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void computeTotal() {
        if (availableQuantity == null) availableQuantity = 0;
        if (reservedQuantity == null) reservedQuantity = 0;
        totalQuantity = availableQuantity + reservedQuantity;
        lastUpdated = LocalDateTime.now();
    }
}
