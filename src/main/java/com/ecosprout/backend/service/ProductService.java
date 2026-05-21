package com.ecosprout.backend.service;

import com.ecosprout.backend.model.Category;
import com.ecosprout.backend.model.Product;
import com.ecosprout.backend.repository.CategoryRepository;
import com.ecosprout.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Creates and saves a new product, now with category linking.
     */
    public Product createProduct(String name, String description, BigDecimal price, int stockQuantity, String imageUrl, List<Long> categoryIds) {

        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setDescription(description);
        newProduct.setPrice(price);
        newProduct.setStockQuantity(stockQuantity);
        newProduct.setImageUrl(imageUrl);
        newProduct.setCreatedAt(Instant.now());

        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = categoryRepository.findByIdIn(categoryIds);
            newProduct.setCategories(categories);
        }

        return productRepository.save(newProduct);
    }

    // --- NEW METHOD ---
    /**
     * Updates the categories for an existing product.
     */
    public Product updateProductCategories(Long productId, List<Long> categoryIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        Set<Category> categories = categoryRepository.findByIdIn(categoryIds);
        product.setCategories(categories);
        return productRepository.save(product);
    }
    // --- END NEW METHOD ---

    /**
     * Updates the stock quantity of an existing product.
     */
    public Product updateProductStock(Long productId, int newQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setStockQuantity(newQuantity);
        return productRepository.save(product);
    }


    /**
     * Deletes a product from the database.
     */
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found: " + productId);
        }
        try {
            productRepository.deleteById(productId);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete product. It may be part of an existing order.", e);
        }
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword != null) {
            return productRepository.searchProducts(keyword);
        }
        return productRepository.findAll();
    }
}