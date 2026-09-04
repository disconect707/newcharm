package org.example.charm.product.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private String description;
    private Integer leftoverVolumeMl;
    private List<ProductVolumePriceDto> volumePrices;
}