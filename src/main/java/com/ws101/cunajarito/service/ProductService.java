package com.ws101.cunajarito.service;

import com.example.demo.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final Map<String, Product> productDatabase = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    
    public ProductService() {
        initializeSampleProducts();
    }
    
    private void initializeSampleProducts() {
        // Initialize with at least 10 products as required
        createProduct(new Product(null, "Laptop", "High-performance laptop", 
            999.99, "Electronics", 10, "laptop.jpg"));
        createProduct(new Product(null, "Mouse", "Wireless mouse", 
            29.99, "Electronics", 50, "mouse.jpg"));
        createProduct(new Product(null, "Keyboard", "Mechanical keyboard", 
            89.99, "Electronics", 30, "keyboard.jpg"));
        createProduct(new Product(null, "Monitor", "27-inch 4K monitor", 
            399.99, "Electronics", 15, "monitor.jpg"));
        createProduct(new Product(null, "Desk Chair", "Ergonomic office chair", 
            199.99, "Furniture", 8, "chair.jpg"));
        createProduct(new Product(null, "Coffee Maker", "Automatic coffee machine", 
            79.99, "Appliances", 20, "coffee.jpg"));
        createProduct(new Product(null, "Running Shoes", "Lightweight sports shoes", 
            89.99, "Sports", 25, "shoes.jpg"));
        createProduct(new Product(null, "Backpack", "Durable travel backpack", 
            49.99, "Accessories", 35, "backpack.jpg"));
        createProduct(new Product(null, "Desk Lamp", "LED desk lamp with dimmer", 
            34.99, "Furniture", 18, "lamp.jpg"));
        createProduct(new Product(null, "Water Bottle", "Insulated stainless steel", 
            24.99, "Accessories", 45, "bottle.jpg"));
        createProduct(new Product(null, "Headphones", "Noise-cancelling headphones", 
            149.99, "Electronics", 12, "headphones.jpg"));
        createProduct(new Product(null, "Yoga Mat", "Non-slip exercise mat", 
            29.99, "Sports", 40, "yoga.jpg"));
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(productDatabase.values());
    }
    
    public Optional<Product> getProductById(String id) {
        return Optional.ofNullable(productDatabase.get(id));
    }
    
    public Product createProduct(Product product) {
        String newId = String.valueOf(idCounter.getAndIncrement());
        product.setId(newId);
        productDatabase.put(newId, product);
        return product;
    }
    
    public Optional<Product> updateProduct(String id, Product product) {
        if (productDatabase.containsKey(id)) {
            product.setId(id);
            productDatabase.put(id, product);
            return Optional.of(product);
        }
        return Optional.empty();
    }
    
    public boolean deleteProduct(String id) {
        return productDatabase.remove(id) != null;
    }
    
    // ========== FILTERING METHODS ==========
    
    // Filter by category
    public List<Product> filterByCategory(String category) {
        return productDatabase.values().stream()
                .filter(product -> product.getCategory() != null && 
                                  product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    // Filter by price range
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        return productDatabase.values().stream()
                .filter(product -> product.getPrice() >= minPrice && 
                                  product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
    // Filter by name (contains search)
    public List<Product> filterByName(String nameKeyword) {
        return productDatabase.values().stream()
                .filter(product -> product.getName() != null && 
                                  product.getName().toLowerCase().contains(nameKeyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // Filter by minimum stock quantity
    public List<Product> filterByMinimumStock(int minStock) {
        return productDatabase.values().stream()
                .filter(product -> product.getStockQuantity() >= minStock)
                .collect(Collectors.toList());
    }
    
    // Advanced combined filter with multiple criteria
    public List<Product> filterProducts(String category, Double minPrice, Double maxPrice, 
                                        String nameKeyword, Integer minStock) {
        Stream<Product> stream = productDatabase.values().stream();
        
        if (category != null && !category.isEmpty()) {
            stream = stream.filter(product -> product.getCategory() != null && 
                                             product.getCategory().equalsIgnoreCase(category));
        }
        
        if (minPrice != null) {
            stream = stream.filter(product -> product.getPrice() >= minPrice);
        }
        
        if (maxPrice != null) {
            stream = stream.filter(product -> product.getPrice() <= maxPrice);
        }
        
        if (nameKeyword != null && !nameKeyword.isEmpty()) {
            stream = stream.filter(product -> product.getName() != null && 
                                             product.getName().toLowerCase().contains(nameKeyword.toLowerCase()));
        }
        
        if (minStock != null) {
            stream = stream.filter(product -> product.getStockQuantity() >= minStock);
        }
        
        return stream.collect(Collectors.toList());
    }
}