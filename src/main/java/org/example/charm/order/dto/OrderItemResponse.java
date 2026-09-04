package org.example.charm.order.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Integer volumeMl;
    private Integer quantity;
    private BigDecimal price; // Цена за единицу на момент покупки
}