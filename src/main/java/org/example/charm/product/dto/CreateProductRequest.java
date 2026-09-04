package org.example.charm.product.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private String name;
    private String brand;
    private String description;
    private Integer totalVolumeMl;
    private Integer leftoverVolumeMl;
    private List<ProductVolumePriceDto> volumePrices;
}