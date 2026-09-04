package org.example.charm.order.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private Long productId;
    private Integer volumeMl; // Какой объем выбрал клиент
    private Integer quantity; // Сколько штук
}