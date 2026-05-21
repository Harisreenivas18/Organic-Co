package com.ecosprout.backend.controller;

import com.ecosprout.backend.model.Category;
import com.ecosprout.backend.model.Product;
import com.ecosprout.backend.service.CategoryService;
import com.ecosprout.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Map<String, Object> payload) {

        String name = (String) payload.get("name");
        String description = (String) payload.get("description");
        BigDecimal price = new BigDecimal(payload.get("price").toString());
        int stockQuantity = Integer.parseInt(payload.get("stockQuantity").toString());
        String imageUrl = (String) payload.get("imageUrl");

        List<Long> categoryIds = ((List<?>) payload.get("categoryIds")).stream()
                .map(id -> Long.parseLong(id.toString()))
                .collect(Collectors.toList());

        Product newProduct = productService.createProduct(name, description, price, stockQuantity, imageUrl, categoryIds);

        return ResponseEntity.ok(newProduct);
    }


    @PutMapping("/products/{id}/categories")
    public ResponseEntity<Product> updateProductCategories(@PathVariable Long id, @RequestBody Map<String, List<Long>> payload) {
        try {
            List<Long> categoryIds = payload.get("categoryIds");
            Product updatedProduct = productService.updateProductCategories(id, categoryIds);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            int newQuantity = Integer.parseInt(payload.get("quantity").toString());
            Product updatedProduct = productService.updateProductStock(id, newQuantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String description = payload.get("description");
            Category newCategory = categoryService.createCategory(name, description);
            return ResponseEntity.ok(newCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String description = payload.get("description");
            Category updatedCategory = categoryService.updateCategory(id, name, description);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}