package org.example.charm.product.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVolumePriceDto {
    private Integer volumeMl;
    private BigDecimal price;
}