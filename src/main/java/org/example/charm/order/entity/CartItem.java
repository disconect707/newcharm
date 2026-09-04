package org.example.charm.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.charm.product.entity.Product;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Ссылка на конкретный флакон духов
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Выбранный объем атомайзера (например, 2мл или 5мл)
    @Column(name = "volume_ml", nullable = false)
    private Integer volumeMl;

    // Сколько атомайзеров этого объема хочет купить клиент
    @Column(nullable = false)
    private Integer quantity;
}