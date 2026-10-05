package com.shopflow.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/product-fallback")
    public Mono<ResponseEntity<Map<String, String>>> productFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Product Service is currently unavailable. Please try again later.")));
    }

    @GetMapping("/order-fallback")
    public Mono<ResponseEntity<Map<String, String>>> orderFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Order Service is currently unavailable. Please try again later.")));
    }

    @GetMapping("/payment-fallback")
    public Mono<ResponseEntity<Map<String, String>>> paymentFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Payment Service is currently unavailable. Please try again later.")));
    }
}
