package org.example.charm.product.service;

import lombok.RequiredArgsConstructor;
import org.example.charm.product.dto.CreateProductRequest;
import org.example.charm.product.dto.ProductResponse;
import org.example.charm.product.dto.ProductVolumePriceDto;
import org.example.charm.product.entity.Product;
import org.example.charm.product.entity.ProductVolumePrice;
import org.example.charm.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Создаем сам флакон парфюма
        Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .description(request.getDescription())
                .totalVolumeMl(request.getTotalVolumeMl())
                .leftoverVolumeMl(request.getLeftoverVolumeMl())
                .build();

        // Добавляем сетку цен для распива (2мл, 5мл, 10мл и т.д.)
        List<ProductVolumePrice> volumePrices = request.getVolumePrices().stream()
                .map(priceDto -> ProductVolumePrice.builder()
                        .product(product)
                        .volumeMl(priceDto.getVolumeMl())
                        .price(priceDto.getPrice())
                        .build())
                .collect(Collectors.toList());

        product.setVolumePrices(volumePrices);

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Парфюм не найден"));
        return mapToResponse(product);
    }

    private ProductResponse mapToResponse(Product product) {
        List<ProductVolumePriceDto> prices = product.getVolumePrices().stream()
                .map(p -> new ProductVolumePriceDto(p.getVolumeMl(), p.getPrice()))
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .leftoverVolumeMl(product.getLeftoverVolumeMl())
                .volumePrices(prices)
                .build();
    }
}