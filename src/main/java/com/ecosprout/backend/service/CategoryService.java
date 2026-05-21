package com.ecosprout.backend.service;

import com.ecosprout.backend.model.Category;
import com.ecosprout.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty.");
        }
        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setDescription(description);
        return categoryRepository.save(newCategory);
    }

    public Category updateCategory(Long categoryId, String newName, String newDescription) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty.");
        }

        category.setName(newName);
        category.setDescription(newDescription);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // Before deleting, we must remove its association with any products
        category.getProducts().forEach(product -> {
            product.getCategories().remove(category);
        });

        // Now it's safe to delete
        categoryRepository.delete(category);
    }
}