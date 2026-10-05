package com.shopflow.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(@Value("${app.product-service-url}") String productServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductsFallback")
    public List<ProductInfo> getProductsByIds(List<Long> ids) {
        log.info("Fetching products with ids: {}", ids);
        return webClient.post()
                .uri("/api/products/batch")
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductInfo>>() {})
                .block();
    }

    public List<ProductInfo> getProductsFallback(List<Long> ids, Exception ex) {
        log.warn("Circuit breaker activated for product-service. Returning empty list. Error: {}", ex.getMessage());
        return Collections.emptyList();
    }
}
