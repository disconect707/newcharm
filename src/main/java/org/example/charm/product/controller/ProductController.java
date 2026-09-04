package org.example.charm.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.charm.product.dto.CreateProductRequest;
import org.example.charm.product.dto.ProductResponse;
import org.example.charm.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Получить список всех доступных парфюмов (для витрины на фронтенде)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Получить детали конкретного парфюма
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // Добавить новый парфюм (позже закроем этот эндпоинт только для ADMIN через Security)
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }
}