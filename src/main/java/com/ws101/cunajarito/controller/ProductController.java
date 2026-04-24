package com.ws101.cunajarito.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Input validation
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getStockQuantity() < 0) {
            return ResponseEntity.badRequest().build();
        }
        
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, 
                                                  @RequestBody Product product) {
        // Input validation
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}