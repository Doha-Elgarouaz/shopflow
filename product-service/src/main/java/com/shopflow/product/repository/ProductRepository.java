package com.shopflow.product.repository;

import com.shopflow.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);
    
    Optional<Product> findBySkuAndActiveTrue(String sku);
    
    List<Product> findByIdIn(List<Long> ids);
    
    boolean existsBySkuAndIdNot(String sku, Long id);
    
    boolean existsBySku(String sku);
}
