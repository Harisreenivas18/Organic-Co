package com.ecosprout.backend.controller;

import com.ecosprout.backend.model.Product;
import com.ecosprout.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Product> searchResults = productService.searchProducts(keyword);
        model.addAttribute("products", searchResults);
        model.addAttribute("keyword", keyword);
        return "shop";
    }
}