package com.ws101.cunajarito.EcommerceApi.service;

import com.ws101.cunajarito.EcommerceApi.exception.ProductNotFoundException;
import com.ws101.cunajarito.EcommerceApi.model.Category;
import com.ws101.cunajarito.EcommerceApi.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Category with id " + id + " not found"));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}
