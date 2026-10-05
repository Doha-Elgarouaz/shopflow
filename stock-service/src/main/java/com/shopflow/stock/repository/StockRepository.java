package com.shopflow.stock.repository;

import com.shopflow.stock.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockItem, Long> {
    Optional<StockItem> findByProductId(Long productId);
}
