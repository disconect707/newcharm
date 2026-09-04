package org.example.charm.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.charm.order.dto.CartItemRequest;
import org.example.charm.order.dto.CartResponse;
import org.example.charm.order.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Временно, пока не настроили Security до конца, будем передавать userId как параметр заголовка или просто мокать
    // В будущем заменим на получение ID из Principal (JWT)
    private Long getCurrentUserId() {
        return 1L; // Заглушка для теста. Позже: Long.parseLong(principal.getName());
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(getCurrentUserId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(getCurrentUserId(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}