package com.shopflow.product.service;

import com.shopflow.product.dto.PageResponse;
import com.shopflow.product.dto.ProductRequest;
import com.shopflow.product.dto.ProductResponse;
import com.shopflow.product.entity.Product;
import com.shopflow.product.exception.ProductNotFoundException;
import com.shopflow.product.kafka.ProductEventProducer;
import com.shopflow.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer eventProducer;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU " + request.sku() + " already exists");
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .sku(request.sku())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .active(request.active() != null ? request.active() : true)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);
        return mapToPageResponse(productPage);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return mapToResponse(product);
    }

    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySkuAndActiveTrue(sku)
                .orElseThrow(() -> new ProductNotFoundException("SKU", sku));
        return mapToResponse(product);
    }

    public PageResponse<ProductResponse> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryAndActiveTrue(category, pageable);
        return mapToPageResponse(productPage);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (productRepository.existsBySkuAndIdNot(request.sku(), id)) {
            throw new IllegalArgumentException("Product with SKU " + request.sku() + " already exists");
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(request.category());
        product.setImageUrl(request.imageUrl());
        if (request.active() != null) {
            product.setActive(request.active());
        }

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateStock(Long id, Integer quantityChange) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        int newQuantity = product.getStockQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock for product " + id);
        }

        product.setStockQuantity(newQuantity);
        Product updated = productRepository.save(product);

        eventProducer.publishStockUpdate(id, newQuantity);

        return mapToResponse(updated);
    }

    public List<ProductResponse> getProductsByIds(List<Long> ids) {
        return productRepository.findByIdIn(ids).stream()
                .filter(Product::getActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getImageUrl(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private PageResponse<ProductResponse> mapToPageResponse(Page<Product> page) {
        List<ProductResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
