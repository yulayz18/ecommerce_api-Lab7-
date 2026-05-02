package com.ws101.cunajarito.EcommerceApi.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
