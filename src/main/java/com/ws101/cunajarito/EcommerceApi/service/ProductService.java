package com.ws101.cunajarito.EcommerceApi.service;

import com.ws101.cunajarito.EcommerceApi.exception.ProductNotFoundException;
import com.ws101.cunajarito.EcommerceApi.model.Category;
import com.ws101.cunajarito.EcommerceApi.model.OrderItem;
import com.ws101.cunajarito.EcommerceApi.model.Product;
import com.ws101.cunajarito.EcommerceApi.repository.CategoryRepository;
import com.ws101.cunajarito.EcommerceApi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service class for product-related operations.
 *
 * Provides business logic for filtering, searching, and managing products.
 * This class acts as an intermediary between the API controller and the
 * data access layer (repository). All product-related transactions are
 * handled within this service.
 *
 * @author Cuna-Jarito Team
 * @version 1.0
 * @see Product
 * @see ProductRepository
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructs a ProductService with required repositories.
     *
     * @param productRepository the repository for accessing product data
     * @param categoryRepository the repository for accessing category data
     */
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all products from the repository.
     *
     * @return a {@code List<Product>} containing all available products.
     *         Returns an empty list if no products exist.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product
     * @return the {@code Product} object with the specified ID
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Filters products based on the specified filter type and value.
     *
     * Supported filter types:
     * - "name": Filters by product name (case-insensitive, partial match)
     * - "category": Filters by category name
     * - "priceRange": Filters by price range (format: "min,max")
     *
     * @param filterType the type of filter to apply (name, category, priceRange)
     * @param filterValue the value to filter by
     * @return a {@code List<Product>} containing products matching the filter criteria.
     *         Returns all products if filter type is not recognized.
     * @see #getAllProducts()
     */
    public List<Product> filterProducts(String filterType, String filterValue) {
        if ("name".equalsIgnoreCase(filterType)) {
            return productRepository.findByNameContainingIgnoreCase(filterValue);
        }
        if ("category".equalsIgnoreCase(filterType)) {
            return productRepository.findByCategory_Name(filterValue);
        }
        if ("priceRange".equalsIgnoreCase(filterType)) {
            String[] parts = filterValue.split(",");
            if (parts.length == 2) {
                double min = parseDouble(parts[0].trim(), 0);
                double max = parseDouble(parts[1].trim(), Double.MAX_VALUE);
                return productRepository.findProductsByPriceBetween(min, max);
            }
        }
        return getAllProducts();
    }

    /**
     * Creates a new product and persists it to the repository.
     *
     * @param product the {@code Product} object to create
     * @return the created {@code Product} with a generated ID
     * @throws IllegalArgumentException if required fields are missing
     */
    public Product createProduct(Product product) {
        product.setCategory(resolveCategory(product.getCategory()));
        return productRepository.save(product);
    }

    /**
     * Updates an entire product (full replacement).
     *
     * @param id the unique identifier of the product to update
     * @param product the new {@code Product} object with updated data
     * @return the updated {@code Product}
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setCategory(resolveCategory(product.getCategory()));
        existing.setStockQuantity(product.getStockQuantity());
        existing.setImageUrl(product.getImageUrl());
        return productRepository.save(existing);
    }

    /**
     * Partially updates a product (only specified fields are updated).
     *
     * Supported update fields: name, description, price, category, stockQuantity, imageUrl
     *
     * @param id the unique identifier of the product to patch
     * @param updates a {@code Map<String, Object>} containing the fields to update
     * @return the partially updated {@code Product}
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    public Product patchProduct(Long id, Map<String, Object> updates) {
        Product existing = getProductById(id);

        updates.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            switch (key) {
                case "name" -> existing.setName(value.toString());
                case "description" -> existing.setDescription(value.toString());
                case "price" -> existing.setPrice(parseDouble(value, existing.getPrice()));
                case "category" -> existing.setCategory(resolveCategoryForPatch(value));
                case "stockQuantity" -> existing.setStockQuantity(parseInt(value, existing.getStockQuantity()));
                case "imageUrl" -> existing.setImageUrl(value.toString());
                default -> {
                }
            }
        });

        return productRepository.save(existing);
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * @param id the unique identifier of the product to delete
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        productRepository.delete(existing);
    }

    private Category resolveCategory(Category category) {
        if (category == null) {
            return null;
        }
        if (category.getId() != null) {
            return categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new ProductNotFoundException("Category with id " + category.getId() + " not found"));
        }
        return categoryRepository.findByNameIgnoreCase(category.getName())
                .orElseGet(() -> categoryRepository.save(new Category(null, category.getName(), null)));
    }

    @SuppressWarnings("unchecked")
    private Category resolveCategoryForPatch(Object value) {
        if (value instanceof Map<?, ?> categoryMap) {
            Object idValue = categoryMap.get("id");
            Object nameValue = categoryMap.get("name");
            if (idValue != null) {
                return categoryRepository.findById(Long.parseLong(idValue.toString()))
                        .orElseThrow(() -> new ProductNotFoundException("Category with id " + idValue + " not found"));
            }
            if (nameValue != null) {
                return categoryRepository.findByNameIgnoreCase(nameValue.toString())
                        .orElseGet(() -> categoryRepository.save(new Category(null, nameValue.toString(), null)));
            }
        } else if (value instanceof String name) {
            return categoryRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> categoryRepository.save(new Category(null, name, null)));
        }
        return null;
    }

    private double parseDouble(Object value, double defaultValue) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private int parseInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
