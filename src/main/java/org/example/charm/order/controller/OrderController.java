package org.example.charm.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.charm.order.dto.OrderResponse;
import org.example.charm.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long getCurrentUserId() {
        return 1L; // Заглушка для теста. Позже будем брать из JWT
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> createOrder() {
        // Вызываем тот самый транзакционный метод оформления и списания
        OrderResponse response = orderService.createOrderFromCart(getCurrentUserId());
        return ResponseEntity.ok(response);
    }
}