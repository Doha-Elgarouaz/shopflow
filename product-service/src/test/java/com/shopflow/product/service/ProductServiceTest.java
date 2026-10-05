package com.shopflow.product.service;

import com.shopflow.product.dto.PageResponse;
import com.shopflow.product.dto.ProductRequest;
import com.shopflow.product.dto.ProductResponse;
import com.shopflow.product.entity.Product;
import com.shopflow.product.exception.ProductNotFoundException;
import com.shopflow.product.kafka.ProductEventProducer;
import com.shopflow.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductEventProducer productEventProducer;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;
    private ProductRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L)
                .name("MacBook Pro M3")
                .description("Apple MacBook Pro with M3 chip")
                .sku("APPLE-MBP-M3")
                .price(new BigDecimal("2999.99"))
                .stockQuantity(10)
                .category("Electronics")
                .active(true)
                .build();

        sampleRequest = new ProductRequest(
                "MacBook Pro M3",
                "Apple MacBook Pro with M3 chip",
                "APPLE-MBP-M3",
                new BigDecimal("2999.99"),
                10,
                "Electronics",
                null
        );
    }

    @Test
    @DisplayName("createProduct — should save and return product response")
    void createProduct_ShouldSaveAndReturnResponse() {
        when(productRepository.existsBySkuAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponse response = productService.createProduct(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("MacBook Pro M3");
        assertThat(response.price()).isEqualByComparingTo("2999.99");
        assertThat(response.category()).isEqualTo("Electronics");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("getProductById — should return product when found")
    void getProductById_WhenProductExists_ShouldReturnResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sku()).isEqualTo("APPLE-MBP-M3");
    }

    @Test
    @DisplayName("getProductById — should throw ProductNotFoundException when not found")
    void getProductById_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("getAllProducts — should return paginated response")
    void getAllProducts_ShouldReturnPageResponse() {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct), PageRequest.of(0, 10), 1);
        when(productRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        PageResponse<ProductResponse> response = productService.getAllProducts(0, 10);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.page()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteProduct — should soft-delete product")
    void deleteProduct_ShouldSoftDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        productService.deleteProduct(1L);

        verify(productRepository).save(argThat(p -> !p.getActive()));
    }

    @Test
    @DisplayName("updateStock — should update quantity and publish event")
    void updateStock_ShouldUpdateQuantityAndPublishEvent() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        productService.updateStock(1L, 25);

        verify(productRepository).save(argThat(p -> p.getStockQuantity() == 25));
        verify(productEventProducer).publishStockUpdate(1L, 25);
    }

    @Test
    @DisplayName("getProductsByIds — should return list of products")
    void getProductsByIds_ShouldReturnList() {
        when(productRepository.findByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));

        List<ProductResponse> responses = productService.getProductsByIds(List.of(1L));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
    }
}
