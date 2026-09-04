package org.example.charm.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_volume_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVolumePrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "volume_ml", nullable = false)
    private Integer volumeMl; // Объем атомайзера (например: 2, 5, 10)

    @Column(nullable = false)
    private BigDecimal price; // Цена за этот объем
}