package org.example.charm.order.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer volumeMl;
    private Integer quantity;
    private BigDecimal itemPrice;  // Цена за 1 флакон этого объема
    private BigDecimal totalPrice; // Итоговая цена (quantity * itemPrice)
}