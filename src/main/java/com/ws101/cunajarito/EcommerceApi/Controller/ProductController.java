package com.ws101.cunajarito.EcommerceApi.Controller;

import com.ws101.cunajarito.EcommerceApi.model.Product;
import com.ws101.cunajarito.EcommerceApi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for product-related HTTP endpoints.
 *
 * Exposes REST API endpoints for CRUD operations and filtering of products.
 * All endpoints return JSON responses with appropriate HTTP status codes.
 * Base path: /api/v1/products
 *
 * @author Cuna-Jarito Team
 * @version 1.0
 * @see Product
 * @see ProductService
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructs a ProductController with the required service.
     *
     * @param productService the service for handling product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     *
     * HTTP GET /api/v1/products
     *
     * @return {@code ResponseEntity} with status 200 OK and a list of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Retrieves a single product by its unique identifier.
     *
     * HTTP GET /api/v1/products/{id}
     *
     * @param id the unique identifier of the product
     * @return {@code ResponseEntity} with status 200 OK and the product object
     * @throws ProductNotFoundException if the product is not found (handled by GlobalExceptionHandler)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Filters products based on filter criteria.
     *
     * HTTP GET /api/v1/products/filter?filterType=<type>&filterValue=<value>
     *
     * Supported filter types: name, category, priceRange
     * Example: /api/v1/products/filter?filterType=priceRange&filterValue=10.0,100.0
     *
     * @param filterType the type of filter to apply (name, category, priceRange)
     * @param filterValue the value to filter by
     * @return {@code ResponseEntity} with status 200 OK and filtered products list
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filter(
            @RequestParam String filterType,
            @RequestParam String filterValue) {

        return ResponseEntity.ok(productService.filterProducts(filterType, filterValue));
    }

    /**
     * Creates a new product.
     *
     * HTTP POST /api/v1/products
     * Content-Type: application/json
     *
     * @param product the product data to create (must include: name, price, category, stockQuantity)
     * @return {@code ResponseEntity} with status 201 Created and the created product
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Replaces an entire product (full update).
     *
     * HTTP PUT /api/v1/products/{id}
     * Content-Type: application/json
     *
     * @param id the unique identifier of the product to update
     * @param product the complete product data to replace existing product
     * @return {@code ResponseEntity} with status 200 OK and the updated product
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestBody Product product) {

        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    /**
     * Partially updates a product (only specified fields are modified).
     *
     * HTTP PATCH /api/v1/products/{id}
     * Content-Type: application/json
     *
     * Example: {"price": 99.99, "stockQuantity": 50}
     *
     * @param id the unique identifier of the product to patch
     * @param updates a map containing fields to update
     * @return {@code ResponseEntity} with status 200 OK and the patched product
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patch(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(productService.patchProduct(id, updates));
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * HTTP DELETE /api/v1/products/{id}
     *
     * @param id the unique identifier of the product to delete
     * @return {@code ResponseEntity} with status 204 No Content
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}