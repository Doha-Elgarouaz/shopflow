package com.shopflow.stock.controller;

import com.shopflow.stock.dto.StockAdjustmentRequest;
import com.shopflow.stock.dto.StockItemResponse;
import com.shopflow.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<Page<StockItemResponse>> getAllStockItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(stockService.getAllStockItems(page, size));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<StockItemResponse> getStockByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStockByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<StockItemResponse> initializeStock(
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam String sku,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(stockService.initializeStock(productId, productName, sku, quantity));
    }

    @PatchMapping("/{productId}/adjust")
    public ResponseEntity<StockItemResponse> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(stockService.adjustStock(productId, request));
    }

    @PatchMapping("/{productId}/restock")
    public ResponseEntity<StockItemResponse> restockProduct(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(stockService.restockProduct(productId, quantity));
    }
}
