package org.example.charm.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    @NotEmpty(message  = "Список товаров в заказе не может быть пустым")
    private List<CartItemRequest> items;

    private String shippingAddress;

    private String comment;
}