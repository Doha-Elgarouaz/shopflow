package com.shopflow.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
    
    public ProductNotFoundException(String field, String value) {
        super("Product not found with " + field + ": " + value);
    }
}
