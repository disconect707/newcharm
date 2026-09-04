package org.example.charm.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.charm.product.entity.Product;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Какой объем распива был куплен (например, 10 мл)
    @Column(name = "volume_ml", nullable = false)
    private Integer volumeMl;

    // Сколько флакончиков такого объема куплено
    @Column(nullable = false)
    private Integer quantity;

    // Фиксируем цену за единицу (за 1 атомайзер) на момент покупки!
    // Это важно: если цены изменятся завтра, история заказов не должна поехать
    @Column(nullable = false)
    private BigDecimal price;
}